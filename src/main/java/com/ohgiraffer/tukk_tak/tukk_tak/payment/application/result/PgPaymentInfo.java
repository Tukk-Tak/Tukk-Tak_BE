package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

/*
 * comment.
 *  PG에 조회한 결제 한 건의 정보 (웹훅을 믿지 않고 PG에 직접 확인한 값)
 * */

public record PgPaymentInfo(
        String paymentKey,
        String orderId,
        String status,          // PG가 알려준 결제 상태 (토스: DONE, CANCELED 등)
        int totalAmount
) {
}
