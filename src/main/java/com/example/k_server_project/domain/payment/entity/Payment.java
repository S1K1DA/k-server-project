package com.example.k_server_project.domain.payment.entity;

import com.example.k_server_project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_imp_uid", columnList = "imp_uid", unique = true)
        }
)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // PortOne 결제 고유번호 (멱등성 체크용)
    @Column(name = "imp_uid", nullable = false, unique = true)
    private String impUid;

    // 결제 금액
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // 결제 생성
    public static Payment createPayment(Long userId, String impUid, Long amount) {
        Payment payment = new Payment();
        payment.userId = userId;
        payment.impUid = impUid;
        payment.amount = amount;
        payment.status = PaymentStatus.READY;
        return payment;
    }

    // 결제 완료
    public void paid() {
        this.status = PaymentStatus.PAID;
    }

    // 결제 실패
    public void failed() {
        this.status = PaymentStatus.FAILED;
    }
}