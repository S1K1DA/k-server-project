package com.example.k_server_project.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long userId;
    private Long menuId;
    private Long totalPrice;

}
