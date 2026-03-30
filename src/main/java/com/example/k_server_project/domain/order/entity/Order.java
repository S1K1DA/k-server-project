package com.example.k_server_project.domain.order.entity;

import com.example.k_server_project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    // 주문 생성
    public static Order createOrder(Long userId, Long menuId, Long totalPrice) {
        Order orders = new Order();
        orders.userId = userId;
        orders.menuId = menuId;
        orders.totalPrice = totalPrice;
        return orders;
    }
}


