package com.example.k_server_project.common.external.portone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortOnePaymentResponse {

    private String id;          // 결제 ID
    private String status;      // 결제 상태 (PAID, FAILED 등)
    private Amount amount;      // 결제 금액
    private String channelKey;  // 채널 키

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private Long total;     // 실제 결제 금액
        private Long paid;      // 결제된 금액
    }
}