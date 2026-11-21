package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;

/*
 * comment.
 *  구독 저장소 포트(인터페이스)
 *  조회와 갱신 메서드는 결제와 PlanPolicyGuard를 만들 때 추가한다.
 * */

public interface SubscriptionRepository {

    // 구독 저장 (지금은 새로 만드는 저장만 지원)
    Subscription save(Subscription subscription);

}
