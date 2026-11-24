package com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.common;

/*
 * comment.
 *  구독 플랜 응답 문구 상수
 * */

public class PlanResponseMessage {

    public static final String AI_LIMIT_EXCEEDED = "이번 기간의 AI 사용 횟수를 모두 사용했습니다.";
    public static final String SAVE_LIMIT_EXCEEDED = "저장할 수 있는 사이트 수를 초과했습니다.";
    public static final String PUBLISH_LIMIT_EXCEEDED = "배포할 수 있는 사이트 수를 초과했습니다.";
    public static final String PUBLISH_NOT_ALLOWED = "현재 플랜에서는 사이트를 배포할 수 없습니다.";
    public static final String EDIT_NOT_ALLOWED = "현재 플랜에서는 사이트를 수정할 수 없습니다.";
    public static final String BUSINESS_NOT_VERIFIED = "사업자 인증을 완료해야 배포할 수 있습니다.";

    private PlanResponseMessage() {
    }

}
