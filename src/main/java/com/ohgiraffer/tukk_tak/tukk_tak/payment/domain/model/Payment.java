package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/*
 * comment.
 *  결제 도메인 모델 (DB를 모르는 순수 자바 객체)
 *  성공한 결제만 저장하고, 결제 한 건은 새로 만든 구독 한 행과 연결된다.
 * */

@Getter
public class Payment {

    private final Long id;
    private final Long subscriptionId;      // 이 결제로 만든 구독
    private final Long memberId;
    private final int amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String orderId;           // 가맹점 주문번호 (중복 결제 방지)
    private final String pgPaymentKey;      // 토스 paymentKey / 카카오 tid
    private final int months;               // 결제한 개월 수
    private final LocalDateTime paidAt;

    public Payment(Long id, Long subscriptionId, Long memberId, int amount, PaymentMethod method,
                   PaymentStatus status, String orderId, String pgPaymentKey, int months,
                   LocalDateTime paidAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.orderId = orderId;
        this.pgPaymentKey = pgPaymentKey;
        this.months = months;
        this.paidAt = paidAt;
    }

    // PG 승인이 끝난 결제 (항상 PAID)
    public static Payment paid(Long subscriptionId, Long memberId, int amount, PaymentMethod method,
                               String orderId, String pgPaymentKey, int months, LocalDateTime paidAt) {
        return new Payment(null, subscriptionId, memberId, amount, method, PaymentStatus.PAID,
                orderId, pgPaymentKey, months, paidAt);
    }

}
