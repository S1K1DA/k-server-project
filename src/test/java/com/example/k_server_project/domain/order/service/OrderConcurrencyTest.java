package com.example.k_server_project.domain.order.service;

import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import com.example.k_server_project.domain.order.dto.request.OrderRequest;
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
class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Long userId;
    private Long menuId;

    @BeforeEach
    void setUp() {
        // 포인트 5000P 유저 생성
        User user = User.createUser("테스트유저");
        user.chargePoint(5000L);
        userRepository.save(user);
        userId = user.getId();

        // 3000P 메뉴 생성
        Menu menu = Menu.createMenu("아메리카노", 3000L);
        menuRepository.save(menu);
        menuId = menu.getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void 포인트_부족한데_동시에_2번_주문시_1번만_성공() throws InterruptedException {

        int threadCount = 2;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 2개 스레드가 동시에 주문 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.createOrder(userId, new OrderRequest(menuId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());

        // 5000P로 3000P짜리 2개 주문 → 1번만 성공해야 함!
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        // 남은 포인트 확인
        User user = userRepository.findById(userId).orElseThrow();
        System.out.println("남은 포인트: " + user.getPoint());
        assertThat(user.getPoint()).isEqualTo(2000L);
    }
}