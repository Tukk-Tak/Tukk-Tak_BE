package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

/*
 * comment.
 *  PG가 승인한 결과
 *  금액은 PG가 실제로 승인한 값이라, 서버 주문 금액과 다시 대조하는 데 쓴다.
 * */

public record PgApproval(
        String paymentKey,      // 토스 paymentKey / 카카오 tid
        int amount              // PG가 승인한 금액
) {
}
