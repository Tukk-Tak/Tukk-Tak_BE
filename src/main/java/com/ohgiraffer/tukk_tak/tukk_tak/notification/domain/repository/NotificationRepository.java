package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository {
    Notification save(Notification notification);

    List<NotificationInfo> findAllByMemberId(Long memberId);

    record NotificationInfo(
            Long id,
            NotificationType type,
            Long targetId,
            String message,
            boolean isRead,
            LocalDateTime createdAt
    ) {}
}
