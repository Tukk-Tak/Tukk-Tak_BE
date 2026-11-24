package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByMemberIdOrderByCreatedAtDescIdDesc(Long memberId);

    boolean existsByMemberIdAndReadFalse(Long memberId);

    @Modifying
    @Query("DELETE FROM NotificationJpaEntity n WHERE n.id IN :ids AND n.memberId = :memberId")
    int deleteByIdsAndMemberId(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM NotificationJpaEntity n WHERE n.createdAt < :threshold")
    int deleteByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);

    boolean existsByIdIn(List<Long> ids);
}
