package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception;

/*
 * comment.
 *  결제 도메인에서 발생하는 실패 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 PaymentExceptionHandler가 변환한다.
 * */

public enum PaymentFailure {
    PLAN_NOT_PURCHASABLE,   // 구매할 수 없는 플랜 (FREE) (400)
    MONTHS_INVALID,         // 개월 수가 1 미만이거나 허용 상한 초과 (400)
    GATEWAY_UNAVAILABLE,    // PG(카카오페이) 호출 실패, 타임아웃, 비정상 응답 (503)
}
