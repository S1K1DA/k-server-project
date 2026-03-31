package com.example.k_server_project.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "결제 ID는 필수입니다.")
    private String paymentId;

    @NotNull(message = "충전 금액은 필수입니다.")
    private Long amount;
}
