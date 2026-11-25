package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.exception;

/*
 * comment.
 *  구독 플랜 전용 예외
 *  - 실패 종류(PlanFailure)만 들고 있고, 상태코드/문구는 PlanExceptionHandler가 정한다.
 *  - 한도 초과는 사용자가 자주 만나는 정상 흐름이라 스택트레이스를 만들지 않는다.
 * */

public class PlanException extends RuntimeException {

    private final PlanFailure failure;

    private PlanException(PlanFailure failure) {
        super(failure.name(), null, false, false);   // 스택트레이스 생략
        this.failure = failure;
    }

    public PlanFailure getFailure() {
        return failure;
    }

    // 이번 주기 AI 사용 횟수를 모두 쓴 경우
    public static PlanException aiLimitExceeded() { return new PlanException(PlanFailure.AI_LIMIT_EXCEEDED); }

    // 저장 가능한 사이트 수를 넘긴 경우
    public static PlanException saveLimitExceeded() { return new PlanException(PlanFailure.SAVE_LIMIT_EXCEEDED); }

    // 배포 가능한 사이트 수를 넘긴 경우
    public static PlanException publishLimitExceeded() { return new PlanException(PlanFailure.PUBLISH_LIMIT_EXCEEDED); }

    // 배포가 허용되지 않는 플랜인 경우
    public static PlanException publishNotAllowed() { return new PlanException(PlanFailure.PUBLISH_NOT_ALLOWED); }

    // 수정이 허용되지 않는 플랜인 경우
    public static PlanException editNotAllowed() { return new PlanException(PlanFailure.EDIT_NOT_ALLOWED); }

    // 사업자 인증을 하지 않은 회원이 배포하려는 경우
    public static PlanException businessNotVerified() { return new PlanException(PlanFailure.BUSINESS_NOT_VERIFIED); }

    // AI 사용에 동의하지 않은 회원이 AI 기능을 쓰려는 경우
    public static PlanException aiConsentRequired() { return new PlanException(PlanFailure.AI_CONSENT_REQUIRED); }

}
