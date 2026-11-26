package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command;

/*
 * comment.
 *  토스 웹훅 처리 명령
 *  본문에서는 이벤트 종류와 결제키만 쓰고, 나머지 값(상태, 주문번호)은 PG에 조회해서 정한다.
 * */

public record TossWebhookCommand(
        String eventType,
        String paymentKey
) {
}
