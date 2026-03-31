package com.example.k_server_project.domain.payment.controller;

import com.example.k_server_project.common.response.BaseResponse;
import com.example.k_server_project.domain.payment.dto.request.PaymentRequest;
import com.example.k_server_project.domain.payment.dto.response.PaymentResponse;
import com.example.k_server_project.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 완료 후 포인트 충전
    @PostMapping("/charge/{userId}")
    public ResponseEntity<BaseResponse<PaymentResponse>> chargePoint(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = paymentService.verifyAndChargePoint(userId, request);

        return ResponseEntity.ok(
                BaseResponse.success("200", "포인트 충전 성공", response)
        );
    }
}