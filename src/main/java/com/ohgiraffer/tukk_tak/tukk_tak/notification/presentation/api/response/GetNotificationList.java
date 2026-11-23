package com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationQueryUsecase;

import java.time.LocalDateTime;

public record GetNotificationList(
        Long id,
        NotificationType type,
        Long targetId,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static GetNotificationList from(NotificationQueryUsecase.NotificationSearchResult r) {
        return new GetNotificationList(r.id(), r.type(),r.targetId(), r.message(), r.isRead(), r.createdAt());
    }
}
