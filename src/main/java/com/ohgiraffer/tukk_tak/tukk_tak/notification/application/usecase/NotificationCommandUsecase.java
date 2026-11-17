package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;

public interface NotificationCommandUsecase {

    void create(CreateNotificationCommand command);

    record CreateNotificationCommand(
            Long memberId,
            NotificationType type,
            Long targetId,
            String message
    ) {}
}
