package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception.InvalidNotificationException;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception.NotificationForbiddenException;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception.NotificationNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional
    public void deleteNotifications(List<Long> notificationIds, Long memberId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            throw new InvalidNotificationException("삭제할 알림을 선택해주세요.");
        }

        int deleted = notificationRepository.deleteByIdsAndMemberId(notificationIds, memberId);
        if (deleted == 0) {
            if (notificationRepository.existsAnyByIds(notificationIds)) {
                throw new NotificationForbiddenException("본인의 알림만 삭제할 수 있어요.");
            }
            throw new NotificationNotFoundException("삭제할 알림을 찾을 수 없어요.");
        }
    }
}
