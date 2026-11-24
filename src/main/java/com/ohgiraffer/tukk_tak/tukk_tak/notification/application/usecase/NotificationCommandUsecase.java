package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

import java.util.List;

public interface NotificationCommandUsecase {

    Notification create(CreateNotificationCommand command);

    void markAsRead(Long notificationId, Long memberId);

    void deleteNotifications(List<Long> notificationIds, Long memberId);

    record CreateNotificationCommand(
            Long memberId,
            NotificationType type,
            Long targetId,
            String message
    ) {}
}
