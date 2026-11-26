package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception;

/*
 * comment.
 *  결제 도메인에서 발생하는 실패 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 PaymentExceptionHandler가 변환한다.
 * */

public enum PaymentFailure {
    PLAN_NOT_PURCHASABLE,       // 구매할 수 없는 플랜 (FREE) (400)
    MONTHS_INVALID,             // 개월 수가 1 미만이거나 허용 상한 초과 (400)
    GATEWAY_UNAVAILABLE,        // PG(카카오페이) 호출 실패, 타임아웃, 비정상 응답 (503)
    PENDING_NOT_FOUND,          // 진행 중인 주문이 없거나 만료됨 (400)
    ORDER_MISMATCH,             // 요청이 서버가 저장한 주문과 다름 (400)
    GATEWAY_REJECTED,           // PG가 승인을 거절함 (400)
    IN_PROGRESS,                // 같은 회원의 결제가 이미 처리 중 (409)
    ALREADY_PROCESSED,          // 이미 처리된 주문 (409)
    ACTIVATION_FAILED,          // 승인 뒤 이용권 적용에 실패해 승인을 취소함 (500)
}
