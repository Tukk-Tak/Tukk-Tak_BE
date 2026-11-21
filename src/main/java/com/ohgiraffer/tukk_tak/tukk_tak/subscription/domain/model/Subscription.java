package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private final SubscriptionStatus status;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime canceledAt;     // 해지 신청 시각 (없으면 null)

    public Subscription(Long id, Long memberId, PlanName planName, int aiUsageCount,
                        SubscriptionStatus status, LocalDate startDate, LocalDate endDate,
                        LocalDateTime canceledAt) {
        this.id = id;
        this.memberId = memberId;
        this.planName = planName;
        this.aiUsageCount = aiUsageCount;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.canceledAt = canceledAt;
    }

    // 가입할 때 자동으로 만드는 FREE 구독 (AI 사용 0회, ACTIVE, 만료 없음)
    public static Subscription free(Long memberId, LocalDate startDate) {
        return new Subscription(null, memberId, PlanName.FREE, 0,
                SubscriptionStatus.ACTIVE, startDate, NO_EXPIRATION, null);
    }

}
