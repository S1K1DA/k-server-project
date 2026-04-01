package com.example.k_server_project.domain.order.repository;

import com.example.k_server_project.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}