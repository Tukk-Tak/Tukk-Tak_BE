package com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.event;

import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationCreatingEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port.NotificationSender;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
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
    private final NotificationSender notificationSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatingEvent event) {

        Notification notification;
        // 이벤트 받고서 db에 알림 내용 저장
        try {
            notification = notificationCommandUsecase.create(new NotificationCommandUsecase.CreateNotificationCommand(
                    event.memberId(), event.type(), event.targetId(), event.message()));

        } catch (Exception e) {
            log.warn("알림 처리 실패: memberId={}, type={}", event.memberId(), event.type(), e);
            return;
        }

        // 저장 후 실제 알림 발송
        try {
            notificationSender.send(notification);
        } catch (Exception e) {
            log.warn("알림 발송 실패: notificationId={}, memberId={}", notification.getId(), event.memberId(), e);
        }
    }
}