package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence;


import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "target_id")
    private Long targetId;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static NotificationJpaEntity fromDomain(Notification notification) {
        NotificationJpaEntity entity = new NotificationJpaEntity();
        entity.id = notification.getId();
        entity.memberId = notification.getMemberId();
        entity.type = notification.getType();
        entity.targetId = notification.getTargetId();
        entity.message = notification.getMessage();
        entity.read = notification.isRead();
        entity.createdAt = notification.getCreatedAt();
        return entity;
    }

    public Notification toDomain() {
        return Notification.builder()
                .id(id)
                .memberId(memberId)
                .type(type)
                .targetId(targetId)
                .message(message)
                .read(read)
                .createdAt(createdAt)
                .build();
    }
}