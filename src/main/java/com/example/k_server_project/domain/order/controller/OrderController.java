package com.example.k_server_project.domain.order.controller;

import com.example.k_server_project.common.response.BaseResponse;
import com.example.k_server_project.domain.order.dto.request.OrderRequest;
import com.example.k_server_project.domain.order.dto.response.OrderResponse;
import com.example.k_server_project.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문/ 포인트 결제
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("201", "주문 성공", response));
    }
}