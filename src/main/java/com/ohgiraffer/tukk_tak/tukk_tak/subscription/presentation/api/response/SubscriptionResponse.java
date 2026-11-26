package com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanLimits;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.SubscriptionInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.util.List;

/*
 * comment.
 *  내 구독 조회 응답
 *  FREE는 endDate와 remainingDays가 null이고 unlimited가 true이다.
 * */

public record SubscriptionResponse(
        CurrentResponse current,
        List<QueuedResponse> queued,
        LimitsResponse limits,
        UsageResponse usage
) {
    public static SubscriptionResponse from(SubscriptionInfoResult result) {
        SubscriptionInfoResult.Current c = result.current();
        PlanLimits l = result.limits();
        SubscriptionInfoResult.Usage u = result.usage();

        return new SubscriptionResponse(
                new CurrentResponse(c.planName(), c.startDate(), c.endDate(), c.remainingDays(), c.unlimited()),
                result.queued().stream()
                        .map(q -> new QueuedResponse(q.planName(), q.startDate(), q.endDate(), q.paused()))
                        .toList(),
                new LimitsResponse(l.aiUsageLimit(), l.aiResets(), l.publishLimit(), l.saveLimit(), l.canEdit()),
                new UsageResponse(u.aiUsed(), u.publishedCount(), u.savedCount()));
    }

    // 지금 적용 중인 구독
    public record CurrentResponse(
            PlanName planName,
            LocalDate startDate,
            LocalDate endDate,          // FREE는 null
            Integer remainingDays,      // 오늘 포함 남은 일수, FREE는 null
            boolean unlimited           // FREE는 true
    ) {
    }

    // 대기 중인 구독
    public record QueuedResponse(
            PlanName planName,
            LocalDate startDate,
            LocalDate endDate,
            boolean paused              // true면 상위 플랜 때문에 멈춰 있는 구독, false면 시작 전 예약 구독
    ) {
    }

    // 현재 플랜의 한도
    public record LimitsResponse(
            int aiUsageLimit,           // 한 주기에 쓸 수 있는 AI 횟수
            boolean aiResets,           // true면 월 주기로 리셋, false면 평생 한도 (FREE)
            int publishLimit,           // 동시에 배포할 수 있는 사이트 수 (0이면 배포 불가)
            int saveLimit,              // 저장할 수 있는 사이트 수
            boolean canEdit             // 수정 가능 여부
    ) {
    }

    // 현재 사용량
    public record UsageResponse(
            int aiUsed,                 // 이번 주기에 쓴 AI 횟수
            int publishedCount,         // 배포 중인 사이트 수
            int savedCount              // 저장한 사이트 수
    ) {
    }
}
