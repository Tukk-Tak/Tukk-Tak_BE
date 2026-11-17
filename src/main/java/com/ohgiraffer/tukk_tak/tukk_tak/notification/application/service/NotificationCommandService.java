package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
}
