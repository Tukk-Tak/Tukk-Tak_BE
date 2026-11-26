package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * comment.
 *  payment 테이블과 매핑되는 JPA 엔티티
 *  이 테이블에는 created_at, updated_at이 없어서 BaseTimeEntity를 상속하지 않는다. (결제 시각은 paid_at)
 * */

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "pg_payment_key", nullable = false, length = 200)
    private String pgPaymentKey;

    @Column(nullable = false)
    private int months;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    private PaymentJpaEntity(Payment p) {
        this.id = p.getId();
        this.subscriptionId = p.getSubscriptionId();
        this.memberId = p.getMemberId();
        this.amount = p.getAmount();
        this.method = p.getMethod();
        this.status = p.getStatus();
        this.orderId = p.getOrderId();
        this.pgPaymentKey = p.getPgPaymentKey();
        this.months = p.getMonths();
        this.paidAt = p.getPaidAt();
    }

    public static PaymentJpaEntity fromDomain(Payment p) { return new PaymentJpaEntity(p); }

    public Payment toDomain() {
        return new Payment(id, subscriptionId, memberId, amount, method, status, orderId,
                pgPaymentKey, months, paidAt);
    }


}
