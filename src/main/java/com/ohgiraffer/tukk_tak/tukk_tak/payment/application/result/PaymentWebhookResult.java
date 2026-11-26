package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

/*
 * comment.
 *  웹훅 처리 결과
 *  PROCESSED와 IGNORED는 PG에 200을 주고, RETRY는 200이 아닌 응답을 줘서 PG가 나중에 다시 보내게 한다.
 * */

public enum PaymentWebhookResult {
    PROCESSED,      // 취소를 반영함
    IGNORED,        // 할 일이 없음 (이미 반영됨, 관심 없는 이벤트, 우리 결제가 아님)
    RETRY           // 아직 판단할 수 없으니 나중에 다시 보내 달라고 한다
}
