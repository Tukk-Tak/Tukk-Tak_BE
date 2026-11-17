package com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.event;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationCreatingEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationCommandUsecase notificationCommandUsecase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatingEvent event) {
        try {
            notificationCommandUsecase.create(new NotificationCommandUsecase.CreateNotificationCommand(
                    event.memberId(), event.type(), event.targetId(), event.message()));
        } catch (Exception e) {
            log.warn("알림 처리 실패: memberId={}, type={}", event.memberId(), event.type(), e);
        }
    }
}