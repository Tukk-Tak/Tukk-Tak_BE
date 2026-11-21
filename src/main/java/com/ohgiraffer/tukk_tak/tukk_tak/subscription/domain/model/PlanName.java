package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model;

/*
 * comment.
 *  구독 플랜 ENUM (한도는 DB가 아니라 PlanPolicyGuard에서 관리한다)
 * */

public enum PlanName {
    FREE,
    BASIC,
    PREMIUM
}
