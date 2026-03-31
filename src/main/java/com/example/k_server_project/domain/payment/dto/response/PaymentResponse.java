package com.example.k_server_project.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long userId;
    private Long chargedAmount;
    private Long totalPoint;
}