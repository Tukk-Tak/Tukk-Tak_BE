package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command;

/*
 * comment.
 *  PG 승인 요청 값 (서버가 저장한 주문 기준)
 *  금액은 클라이언트 값이 아니라 서버가 저장한 주문 금액이고, 결제수단에 없는 값은 null이다.
 * */

public record PgApproveCommand(
        String orderId,
        Long memberId,
        int amount,
        String paymentKey,      // 토스 결제키
        String kakaoTid,        // 카카오 결제 준비에서 받은 tid
        String pgToken          // 카카오 승인 토큰
) {
}
