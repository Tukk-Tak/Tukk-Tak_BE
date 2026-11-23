package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository springDataNotificationRepository;

    @Override
    public Notification save(Notification notification) {
        return springDataNotificationRepository
                .save(NotificationJpaEntity.fromDomain(notification))
                .toDomain();
    }

    @Override
    public List<NotificationInfo> findAllByMemberId(Long memberId) {
        return springDataNotificationRepository
                .findByMemberIdOrderByCreatedAtDescIdDesc(memberId)
                .stream()
                .map(e -> new NotificationInfo(
                        e.getId(), e.getType(), e.getTargetId(), e.getMessage(), e.isRead(), e.getCreatedAt()))
                .toList();
    }
}
