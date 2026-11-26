package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model;

/*
 * comment.
 *  구독 상태 ENUM (ACTIVE: 이용 중, CANCELED: 취소됨(결제 취소 등), EXPIRED: 만료)
 * */

public enum SubscriptionStatus {
    ACTIVE,
    CANCELED,
    EXPIRED
}
