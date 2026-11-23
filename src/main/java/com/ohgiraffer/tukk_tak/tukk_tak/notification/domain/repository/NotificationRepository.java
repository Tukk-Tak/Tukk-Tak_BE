package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    List<NotificationInfo> findAllByMemberId(Long memberId);

    Boolean existsUnreadByMemberId(Long memberId);

    Optional<Notification> findById(Long notificationId);

    record NotificationInfo(
            Long id,
            NotificationType type,
            Long targetId,
            String message,
            boolean isRead,
            LocalDateTime createdAt
    ) {}
}
