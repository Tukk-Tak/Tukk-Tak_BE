package com.ohgiraffer.tukk_tak.tukk_tak.community.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository.CommentInfo;
import org.springframework.stereotype.Component;

@Component
public class CommentSearchPolicy {

    // 부모 댓글
    // 1. 삭제되지 않았고
    // 2. 공개
    // 3. 비공개일거면 게시글 작성자 or 댓글 작성자 여야함
    public boolean canViewComment(CommentInfo comment, Long viewerId, Long postWriterId) {
        return canView(comment, viewerId, postWriterId, null);
    }

    // 대댓글 - 부모 댓글 작성자도 볼 수 있음
    public boolean canViewReply(CommentInfo reply, Long viewerId, Long postWriterId, Long parentWriterId) {
        return canView(reply, viewerId, postWriterId, parentWriterId);
    }

    // 비공개 부모 댓글의 대댓글 목록은 게시글 작성자 + 부모 댓글 작성자만 조회 가능
    public boolean canViewReplies(CommentInfo parent, Long viewerId, Long postWriterId) {
        return !parent.isPrivate()
                || isSameMember(viewerId, postWriterId)
                || isSameMember(viewerId, parent.memberId());
    }

    // 댓글/대댓글 공통 조회 조건
    private boolean canView(CommentInfo comment, Long viewerId, Long postWriterId, Long parentWriterId) {
        if (comment.isDeleted()) {
            return false;
        }
        if (!comment.isPrivate()) {
            return true;
        }
        return isSameMember(viewerId, postWriterId)
                || isSameMember(viewerId, comment.memberId())
                || isSameMember(viewerId, parentWriterId);
    }

    // 비로그인은 항상 false
    private boolean isSameMember(Long viewerId, Long targetId) {
        return viewerId != null && viewerId.equals(targetId);
    }
}