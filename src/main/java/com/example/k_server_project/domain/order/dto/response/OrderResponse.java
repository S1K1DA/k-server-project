package com.example.k_server_project.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private Long menuId;
    private Long totalPrice;
    private Long remainPoint;
}