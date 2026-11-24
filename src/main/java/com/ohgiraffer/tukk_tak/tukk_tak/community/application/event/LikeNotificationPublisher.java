package com.ohgiraffer.tukk_tak.tukk_tak.community.application.event;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationCreatingEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeNotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    // 글 작성자에게 알림
    public void publish(Post post, Long likerId) {
        if (post.getMemberId().equals(likerId)) {
            return;
        }
        eventPublisher.publishEvent(new NotificationCreatingEvent(
                post.getMemberId(), NotificationType.LIKE, post.getId(), "내 게시글에 좋아요가 눌렸어요."));
    }
}