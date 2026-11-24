package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

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


}
