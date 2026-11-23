package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception.NotificationNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService implements NotificationCommandUsecase {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification create(CreateNotificationCommand command) {

        // DB에 알림 내용 저장
        Notification notification = notificationRepository.save(
                Notification.create(command.memberId(), command.type(), command.targetId(), command.message()));

        return notification;
    }

    @Override
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("존재하지 않는 알림이에요."));

        notification.markAsRead(memberId);
        notificationRepository.save(notification);
    }
}
