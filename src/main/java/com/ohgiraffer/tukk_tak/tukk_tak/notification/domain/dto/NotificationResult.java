package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.dto;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

import java.time.LocalDateTime;

public record NotificationResult(
        Long id,
        NotificationType type,
        Long targetId,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {

    public static NotificationResult from(Notification notification) {
        return new NotificationResult(
                notification.getId(),
                notification.getType(),
                notification.getTargetId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
