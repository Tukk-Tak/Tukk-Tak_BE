package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CommentRepository {
    List<CommentInfo> findComments(Long postId, int page, int size);

    Map<Long, Long> countRepliesByParentIds(List<Long> list);

    long countComments(Long postId);

    record CommentInfo(
            Long id, Long memberId, String content, LocalDateTime createdAt,
            boolean isPrivate, boolean isDeleted
    ) {}
}
