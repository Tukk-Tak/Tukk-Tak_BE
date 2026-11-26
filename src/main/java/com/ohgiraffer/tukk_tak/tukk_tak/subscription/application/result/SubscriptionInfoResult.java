package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanLimits;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.util.List;

/*
 * comment.
 *  내 구독 조회 결과 (현재 구독, 대기 중인 구독, 플랜 한도, 사용량)
 *  FREE는 만료가 없어서 종료일과 남은 일수를 null로 두고 unlimited로 알린다.
 * */

public record SubscriptionInfoResult(
        Current current,
        List<Queued> queued,
        PlanLimits limits,
        Usage usage
) {
    // 지금 적용 중인 구독 (같은 플랜을 이어서 연장한 기간은 하나로 합쳐 종료일을 계산한다)
    public record Current(
            PlanName planName,
            LocalDate startDate,
            LocalDate endDate,          // FREE는 null
            Integer remainingDays,      // 오늘 포함 남은 일수, FREE는 null
            boolean unlimited           // FREE는 true
    ) {
    }

    // 대기 중인 구독
    public record Queued(
            PlanName planName,
            LocalDate startDate,
            LocalDate endDate,
            boolean paused              // true면 상위 플랜 때문에 멈춰 있는 구독, false면 시작 전 예약 구독
    ) {
    }

    // 현재 사용량 (한도는 limits에 있다)
    public record Usage(
            int aiUsed,                 // 이번 주기에 쓴 AI 횟수
            int publishedCount,         // 배포 중인 사이트 수
            int savedCount              // 저장한 사이트 수
    ) {
    }
}
