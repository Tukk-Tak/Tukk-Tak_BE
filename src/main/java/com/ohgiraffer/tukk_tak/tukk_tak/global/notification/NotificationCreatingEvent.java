package com.ohgiraffer.tukk_tak.tukk_tak.global.notification;

public record NotificationCreatingEvent(
        Long memberId,
        NotificationType type,
        Long targetId,
        String message
) {
}
