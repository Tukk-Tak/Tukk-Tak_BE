package com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.common;

/*
 * comment.
 *  구독 플랜 응답 코드 상수 (도메인마다 자기 응답 코드를 가진다는 팀 규칙)
 * */

public class PlanResponseCode {

    public static final String AI_LIMIT_EXCEEDED = "PLAN-AI-LIMIT-EXCEEDED";
    public static final String SAVE_LIMIT_EXCEEDED = "PLAN-SAVE-LIMIT-EXCEEDED";
    public static final String PUBLISH_LIMIT_EXCEEDED = "PLAN-PUBLISH-LIMIT-EXCEEDED";
    public static final String PUBLISH_NOT_ALLOWED = "PLAN-PUBLISH-NOT-ALLOWED";
    public static final String EDIT_NOT_ALLOWED = "PLAN-EDIT-NOT-ALLOWED";
    public static final String BUSINESS_NOT_VERIFIED = "PLAN-BUSINESS-NOT-VERIFIED";
    public static final String AI_CONSENT_REQUIRED = "PLAN-AI-CONSENT-REQUIRED";
    public static final String SUBSCRIPTION_FETCHED = "SUBSCRIPTION-FETCHED";

    private PlanResponseCode() {
    }

}
