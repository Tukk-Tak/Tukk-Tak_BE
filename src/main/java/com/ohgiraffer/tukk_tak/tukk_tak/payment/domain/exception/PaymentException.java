package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception;

/*
 * comment.
 *  결제 도메인 전용 예외
 *  - 실패 종류(PaymentFailure)만 들고 있고, 상태코드/문구는 PaymentExceptionHandler가 정한다.
 *  - 사용자가 자주 만나는 정상 흐름이라 스택트레이스를 만들지 않는다.
 * */

public class PaymentException extends RuntimeException {

    private final PaymentFailure failure;

    private PaymentException(PaymentFailure failure) {
        super(failure.name(), null, false, false);   // 스택트레이스 생략
        this.failure = failure;
    }

    public PaymentFailure getFailure() {
        return failure;
    }

    // 구매할 수 없는 플랜(FREE)으로 결제를 준비하려는 경우
    public static PaymentException planNotPurchasable() { return new PaymentException(PaymentFailure.PLAN_NOT_PURCHASABLE); }

    // 개월 수가 1 미만이거나 허용 상한을 넘은 경우
    public static PaymentException monthsInvalid() { return new PaymentException(PaymentFailure.MONTHS_INVALID); }

    // PG 호출 실패, 타임아웃, 비정상 응답
    public static PaymentException gatewayUnavailable() { return new PaymentException(PaymentFailure.GATEWAY_UNAVAILABLE); }

    // 진행 중인 주문이 없거나 만료된 경우
    public static PaymentException pendingNotFound() { return new PaymentException(PaymentFailure.PENDING_NOT_FOUND); }

    // 요청이 서버가 저장한 주문과 다른 경우
    public static PaymentException orderMismatch() { return new PaymentException(PaymentFailure.ORDER_MISMATCH); }

    // PG가 승인을 거절한 경우
    public static PaymentException gatewayRejected() { return new PaymentException(PaymentFailure.GATEWAY_REJECTED); }

    // 같은 회원의 결제가 이미 처리 중인 경우
    public static PaymentException inProgress() { return new PaymentException(PaymentFailure.IN_PROGRESS); }

    // 이미 처리된 주문을 다시 승인하려는 경우
    public static PaymentException alreadyProcessed() { return new PaymentException(PaymentFailure.ALREADY_PROCESSED); }

    // 승인 뒤 이용권 적용에 실패한 경우
    public static PaymentException activationFailed() { return new PaymentException(PaymentFailure.ACTIVATION_FAILED); }

}
