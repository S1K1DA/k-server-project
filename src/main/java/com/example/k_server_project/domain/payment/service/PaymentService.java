package com.example.k_server_project.domain.payment.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.common.external.portone.PortOneClient;
import com.example.k_server_project.common.external.portone.PortOnePaymentResponse;
import com.example.k_server_project.domain.payment.dto.request.PaymentRequest;
import com.example.k_server_project.domain.payment.dto.response.PaymentResponse;
import com.example.k_server_project.domain.payment.entity.Payment;
import com.example.k_server_project.domain.payment.repository.PaymentRepository;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${portone.store-id}")
    private String storeId;

    private final PortOneClient portOneClient;
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    // 결제 기능
    public PaymentResponse verifyAndChargePoint(Long userId, PaymentRequest request) {

        String paymentId = request.getPaymentId();
        Long amount = request.getAmount();

        // 멱등성 체크
        if (paymentRepository.existsByImpUid(paymentId)) {
            throw new ServiceErrorException(ErrorEnum.ERR_DUPLICATE_PAYMENT);
        }

        try {
            // PortOne API로 결제 검증
            PortOnePaymentResponse portOnePayment = portOneClient.getPayment(paymentId, storeId);

            // 결제 상태 검증
            if (!"PAID".equals(portOnePayment.getStatus())) {
                throw new ServiceErrorException(ErrorEnum.ERR_PAYMENT_FAILED);
            }

            // 결제 금액 검증
            if (!amount.equals(portOnePayment.getAmount().getTotal())) {
                throw new ServiceErrorException(ErrorEnum.ERR_PAYMENT_AMOUNT_MISMATCH);
            }

            // 결제 스냅샷 저장
            Payment payment = Payment.createPayment(userId, paymentId, amount);
            paymentRepository.save(payment);
            payment.paid();

            // 포인트 충전
            User user = userService.chargePoint(userId, amount);

            log.info("[Payment] 결제 완료 - userId={}, paymentId={}, amount={}", userId, paymentId, amount);

            return new PaymentResponse(userId, amount, user.getPoint());

        } catch (ServiceErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Payment] 결제 검증 실패 - paymentId={}", paymentId, e);
            throw new ServiceErrorException(ErrorEnum.ERR_PAYMENT_FAILED);
        }
    }
}