package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationQueryUsecase {
    List<NotificationSearchResult> getNotificationList(Long memberId);

    Boolean getIsUnreadNotificationExists(Long memberId);

    record NotificationSearchResult(
            Long id,
            NotificationType type,
            Long targetId,
            String message,
            boolean isRead,
            LocalDateTime createdAt
    ){}
}
