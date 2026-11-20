package com.ohgiraffer.tukk_tak.tukk_tak.community.application.event;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreateCommentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationCreatingEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.global.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentNotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(CreateCommentCommand command, Post post, CommentRepository.CommentInfo parent) {
        if (parent == null) {
            notify(post.getMemberId(), command, "내 게시글에 댓글이 달렸어요.");
            return;
        }

        notify(parent.memberId(), command, "내 댓글에 답글이 달렸어요.");
        if (!post.getMemberId().equals(parent.memberId())) {
            notify(post.getMemberId(), command, "내 게시글에 답글이 달렸어요.");
        }
    }

    private void notify(Long receiverId, CreateCommentCommand command, String message) {
        if (receiverId.equals(command.memberId())) {
            return;
        }
        eventPublisher.publishEvent(new NotificationCreatingEvent(
                receiverId, NotificationType.COMMENT, command.postId(), message));
    }
}