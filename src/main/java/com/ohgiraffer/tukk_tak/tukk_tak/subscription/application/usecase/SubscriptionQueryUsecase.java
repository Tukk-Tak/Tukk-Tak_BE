package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.SubscriptionInfoResult;

/*
 * comment.
 *  구독 조회 유스케이스 (마이페이지의 현재 플랜 카드에 쓰는 값)
 * */

public interface SubscriptionQueryUsecase {

    // 내 현재 구독, 대기 중인 구독, 플랜 한도와 사용량을 조회한다
    SubscriptionInfoResult getMySubscription(Long memberId);

}
