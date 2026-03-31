package com.example.k_server_project.common.external.portone;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneClient {

    private final RestClient portOneRestClient;

    // 결제 단건 조회
    public PortOnePaymentResponse getPayment(String paymentId, String storeId) {
        return portOneRestClient.get()
                .uri("/payments/{paymentId}?storeId={storeId}", paymentId, storeId)  // ← storeId 추가!
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String errorBody = new String(res.getBody().readAllBytes());
                    log.error("[PortOne] 결제 조회 실패 - paymentId={}, status={}, body={}",
                            paymentId, res.getStatusCode(), errorBody);
                    throw new ServiceErrorException(ErrorEnum.ERR_PAYMENT_FAILED);
                })
                .body(PortOnePaymentResponse.class);
    }
}