package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (subscription 테이블 조회/저장)
 *  AI 횟수 증감은 한 번의 UPDATE로 처리해서 동시에 눌러도 한도를 넘지 않게 한다.
 * */

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {

    // 오늘 이용 중인 구독 (시작일이 지났고 만료일이 지나지 않은 것)
    @Query("select s from SubscriptionJpaEntity s "
            + "where s.memberId = :memberId and s.status = :status "
            + "and s.startDate <= :today and s.endDate >= :today")
    List<SubscriptionJpaEntity> findInUse(@Param("memberId") Long memberId,
                                          @Param("status") SubscriptionStatus status,
                                          @Param("today") LocalDate today);

    // 지정한 플랜들 중 아직 만료되지 않은 구독의 가장 늦은 만료일 (시작 전인 예약 구독 포함, 없으면 null)
    @Query("select max(s.endDate) from SubscriptionJpaEntity s "
            + "where s.memberId = :memberId and s.status = :status "
            + "and s.planName in :planNames and s.endDate >= :today")
    LocalDate findLatestEndDate(@Param("memberId") Long memberId,
                                @Param("status") SubscriptionStatus status,
                                @Param("planNames") List<PlanName> planNames,
                                @Param("today") LocalDate today);

    // 주기가 바뀌었으면 0부터 다시 세어 +1, 한도 미만일 때만 반영 (영향받은 행이 0이면 한도 초과)
    // MySQL은 SET을 왼쪽부터 처리하므로 횟수 계산이 주기 갱신보다 앞에 있어야 한다 (순서를 바꾸지 말 것)
    @Modifying(clearAutomatically = true)
    @Query("update SubscriptionJpaEntity s "
            + "set s.aiUsageCount = (case when s.usageCycleStart = :cycleStart then s.aiUsageCount else 0 end) + 1, "
            + "s.usageCycleStart = :cycleStart "
            + "where s.id = :id "
            + "and (case when s.usageCycleStart = :cycleStart then s.aiUsageCount else 0 end) < :limit")
    int increaseAiUsage(@Param("id") Long id, @Param("cycleStart") LocalDate cycleStart,
                        @Param("limit") int limit);

    // 같은 주기일 때만 1 되돌린다 (0 아래로 내려가지 않게)
    @Modifying(clearAutomatically = true)
    @Query("update SubscriptionJpaEntity s set s.aiUsageCount = s.aiUsageCount - 1 "
            + "where s.id = :id and s.usageCycleStart = :cycleStart and s.aiUsageCount > 0")
    int decreaseAiUsage(@Param("id") Long id, @Param("cycleStart") LocalDate cycleStart);

    // 낮은 플랜의 기간을 새 이용 기간만큼 한 번의 UPDATE로 민다
    // 이미 시작한 구독은 종료일만, 아직 시작 전인 구독은 시작일과 종료일을 같이 민다 (남은 일수가 그대로 보존된다)
    // MySQL은 SET을 왼쪽부터 처리하지만 두 식이 서로를 참조하지 않아 순서와 무관하다
    @Modifying(clearAutomatically = true)
    @Query(value = "update subscription "
            + "set start_date = case when start_date >= :fromDate "
            + "then date_add(start_date, interval :days day) else start_date end, "
            + "end_date = date_add(end_date, interval :days day) "
            + "where member_id = :memberId and status = 'ACTIVE' "
            + "and plan_name in (:planNames) and end_date >= :fromDate", nativeQuery = true)
    int pauseLowerPlans(@Param("memberId") Long memberId, @Param("planNames") List<String> planNames,
                        @Param("fromDate") LocalDate fromDate, @Param("days") int days);

    // 끝나지 않은 ACTIVE 구독 전부 (회원당 몇 행 안 되므로 한 번에 읽고, 나누는 건 서비스가 한다)
    @Query("select s from SubscriptionJpaEntity s "
            + "where s.memberId = :memberId and s.status = :status and s.endDate >= :today "
            + "order by s.startDate asc")
    List<SubscriptionJpaEntity> findUnexpired(@Param("memberId") Long memberId,
                                              @Param("status") SubscriptionStatus status,
                                              @Param("today") LocalDate today);

}
