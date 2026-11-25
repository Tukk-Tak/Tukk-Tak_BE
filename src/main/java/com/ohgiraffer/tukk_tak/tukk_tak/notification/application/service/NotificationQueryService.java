package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationQueryService implements NotificationQueryUsecase {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationSearchResult> getNotificationList(Long memberId) {

        return notificationRepository.findAllByMemberId(memberId).stream()
                .map(n -> new NotificationSearchResult(
                        n.id(), n.type(), n.targetId(), n.message(), n.isRead(), n.createdAt()))
                .toList();
    }

    @Override
    public Boolean getIsUnreadNotificationExists(Long memberId) {
        return notificationRepository.existsUnreadByMemberId(memberId);
    }
}
