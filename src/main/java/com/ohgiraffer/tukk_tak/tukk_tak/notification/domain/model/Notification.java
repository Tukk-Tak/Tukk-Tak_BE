package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception.InvalidNotificationException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Notification {

    private final Long id;
    private final Long memberId;
    private final NotificationType type;
    private final Long targetId;
    private final String message;
    private final boolean read;
    private final LocalDateTime createdAt;

    @Builder
    private Notification(Long id, Long memberId, NotificationType type, Long targetId,
                         String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.targetId = targetId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    // 알림 생성
    public static Notification create(Long memberId, NotificationType type, Long targetId, String message) {
        if (memberId == null) {
            throw new InvalidNotificationException("알림을 받을 회원이 필요해요.");
        }
        if (type == null) {
            throw new InvalidNotificationException("알림 종류가 필요해요.");
        }
        if (message == null || message.isBlank()) {
            throw new InvalidNotificationException("알림 문구가 필요해요.");
        }

        return Notification.builder()
                .memberId(memberId)
                .type(type)
                .targetId(targetId)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }
}