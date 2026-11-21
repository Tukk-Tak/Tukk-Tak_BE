package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  subscription 테이블과 매핑되는 JPA 엔티티
 *  이 테이블에는 updated_at이 없어서 BaseTimeEntity를 상속하지 않는다. (created_at은 DB 기본값이 채운다)
 * */

@Entity
@Table(name = "subscription")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_name", nullable = false, length = 20)
    private PlanName planName;

    @Column(name = "ai_usage_count", nullable = false)
    private int aiUsageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    private SubscriptionJpaEntity(Subscription s) {
        this.id = s.getId();
        this.memberId = s.getMemberId();
        this.planName = s.getPlanName();
        this.aiUsageCount = s.getAiUsageCount();
        this.status = s.getStatus();
        this.startDate = s.getStartDate();
        this.endDate = s.getEndDate();
        this.canceledAt = s.getCanceledAt();
    }

    public static SubscriptionJpaEntity fromDomain(Subscription s) { return new SubscriptionJpaEntity(s); }

    public Subscription toDomain() {
        return new Subscription(id, memberId, planName, aiUsageCount, status,
                startDate, endDate, canceledAt);
    }

}
