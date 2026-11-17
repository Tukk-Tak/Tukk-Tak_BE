package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
