package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/*
 * comment.
 *  구독 도메인 모델 (DB를 모르는 순수 자바 객체)
 *  FREE는 만료가 없지만 end_date가 NOT NULL이라 9999-12-31 값을 쓴다.
 *  그러면 만료 판정을 "end_date < 오늘" 하나로 할 수 있어 NULL 분기가 필요 없다.
 * */

@Getter
public class Subscription {

    // FREE 플랜의 만료일 (사실상 만료 없음)
    public static final LocalDate NO_EXPIRATION = LocalDate.of(9999, 12, 31);

    private final Long id;
    private final Long memberId;
    private final PlanName planName;
    private final int aiUsageCount;             // 이번 주기 AI 사용 횟수
    private final LocalDate usageCycleStart;    // 위 횟수를 세기 시작한 주기의 시작일 (아직 안 썼으면 null)
    private final SubscriptionStatus status;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime canceledAt;     // 해지 신청 시각 (없으면 null)

    public Subscription(Long id, Long memberId, PlanName planName, int aiUsageCount,
                        LocalDate usageCycleStart, SubscriptionStatus status, LocalDate startDate,
                        LocalDate endDate, LocalDateTime canceledAt) {
        this.id = id;
        this.memberId = memberId;
        this.planName = planName;
        this.aiUsageCount = aiUsageCount;
        this.usageCycleStart = usageCycleStart;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.canceledAt = canceledAt;
    }

    // 가입할 때 자동으로 만드는 FREE 구독 (AI 사용 0회, ACTIVE, 만료 없음)
    public static Subscription free(Long memberId, LocalDate startDate) {
        return new Subscription(null, memberId, PlanName.FREE, 0, null,
                SubscriptionStatus.ACTIVE, startDate, NO_EXPIRATION, null);
    }

    // 결제로 새로 만드는 유료 구독 (AI 사용 0회, ACTIVE)
    public static Subscription paid(Long memberId, PlanName planName, LocalDate startDate, LocalDate endDate) {
        return new Subscription(null, memberId, planName, 0, null,
                SubscriptionStatus.ACTIVE, startDate, endDate, null);
    }

    // 오늘이 속한 월 주기의 시작일: 시작일에서 plusMonths로 계산해서 월말(31일 등)에도 날짜가 밀리지 않는다
    public LocalDate cycleStartOf(LocalDate today) {
        long months = ChronoUnit.MONTHS.between(startDate, today);
        LocalDate cycleStart = startDate.plusMonths(months);
        LocalDate next = startDate.plusMonths(months + 1);
        // 월말 보정으로 다음 주기 시작일이 오늘 이하가 되는 경우(예: 1/31 시작, 2/28)는 다음 주기로 본다
        return next.isAfter(today) ? cycleStart : next;
    }

    // 주어진 주기에서 쓴 AI 횟수 (저장된 주기와 다르면 아직 갱신 전이므로 0)
    public int aiUsageIn(LocalDate cycleStart) {
        return cycleStart.equals(usageCycleStart) ? aiUsageCount : 0;
    }

}
