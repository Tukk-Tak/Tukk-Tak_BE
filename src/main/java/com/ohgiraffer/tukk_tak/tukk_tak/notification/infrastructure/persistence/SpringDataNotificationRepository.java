package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {
    List<NotificationJpaEntity> findByMemberIdOrderByCreatedAtDescIdDesc(Long memberId);

    boolean existsByMemberIdAndReadFalse(Long memberId);
}
