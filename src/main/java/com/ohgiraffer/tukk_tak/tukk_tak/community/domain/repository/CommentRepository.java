package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository {
    List<CommentInfo> findComments(Long postId, int page, int size);

    Map<Long, Long> countRepliesByParentIds(List<Long> list);

    long countComments(Long postId);

    Optional<CommentInfo> findParentComment(Long postId, Long parentCommentId);

    List<CommentInfo> findReplies(Long parentCommentId);

    void save(Comment comment);

    record CommentInfo(
            Long id, Long memberId, String content, LocalDateTime createdAt,
            boolean isPrivate, boolean isDeleted
    ) {}
}
