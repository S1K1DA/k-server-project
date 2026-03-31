package com.example.k_server_project.domain.payment.repository;

import com.example.k_server_project.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 멱등성 체크용
    boolean existsByImpUid(String impUid);
}