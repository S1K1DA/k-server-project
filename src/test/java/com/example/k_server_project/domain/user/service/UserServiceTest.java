package com.example.k_server_project.domain.user.service;

import com.example.k_server_project.domain.payment.repository.PaymentRepository;
import com.example.k_server_project.domain.payment.entity.Payment;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = User.createUser("테스트유저");
        userRepository.save(user);
        userId = user.getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void 다수_서버에서_동시_충전_요청시_포인트_정확하게_처리() throws InterruptedException {

        int threadCount = 10;
        Long chargeAmount = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 10개 스레드 동시에 충전 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userService.chargePoint(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 검증
        User user = userRepository.findById(userId).orElseThrow();
        System.out.println("최종 포인트: " + user.getPoint());

        // 10 * 1000 = 10000 이어야 함!
        assertThat(user.getPoint()).isEqualTo(threadCount * chargeAmount);
    }


    @Test
    void 동시에_같은_paymentId로_중복_결제_요청시_한번만_처리() throws InterruptedException {

        int threadCount = 5;
        String samePaymentId = "test-payment-id-" + System.currentTimeMillis();
        Long amount = 10000L;

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 5개 스레드가 같은 paymentId로 동시에 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // PaymentRepository에 직접 저장 시도 (impUid 중복 체크)
                    if (!paymentRepository.existsByImpUid(samePaymentId)) {
                        Payment payment = Payment.createPayment(userId, samePaymentId, amount);
                        paymentRepository.save(payment);
                        userService.chargePoint(userId, amount);
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("성공: " + successCount.get());
        System.out.println("실패(중복): " + failCount.get());

        // 딱 1번만 성공해야 함!
        assertThat(successCount.get()).isEqualTo(1);

        // 포인트도 한 번만 충전됐어야 함!
        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getPoint()).isEqualTo(amount);
    }
}