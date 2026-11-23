package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
 * comment.
 *  구독 저장소 포트(인터페이스)
 * */

public interface SubscriptionRepository {

    // 구독 저장 (지금은 새로 만드는 저장만 지원)
    Subscription save(Subscription subscription);

    // 오늘 이용 중인 구독들 (ACTIVE이고 시작일이 지났고 만료일이 지나지 않은 것)
    List<Subscription> findInUse(Long memberId, LocalDate today);

    // 지정한 플랜들 중 가장 늦은 만료일 (ACTIVE이고 아직 만료되지 않은 구독, 시작 전인 예약 구독 포함. 없으면 empty)
    Optional<LocalDate> findLatestEndDate(Long memberId, List<PlanName> planNames, LocalDate today);

    // AI 사용 횟수를 원자적으로 1 올린다. 한도에 걸려 못 올리면 false (주기가 바뀌었으면 0부터 다시 센다)
    boolean increaseAiUsage(Long subscriptionId, LocalDate cycleStart, int limit);

    // AI 사용 횟수를 1 되돌린다 (AI 호출이 실패했을 때). 주기가 이미 바뀌었으면 건드리지 않는다
    void decreaseAiUsage(Long subscriptionId, LocalDate cycleStart);

}
