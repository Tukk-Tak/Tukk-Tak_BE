package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentQueryUsecase {
    CommentListResult getComments(Long postId, Long memberId, int page, int size);

    record CommentListResult(
            List<CommentSearchResult> comments, int page, int size,
            long totalElements, int totalPages
    ) {}

    record CommentSearchResult(
            Long id, String nickname, String content, LocalDateTime createdAt,
            Long replyCount, boolean isPrivate, boolean isDeleted, boolean isWriter, boolean isMine
    ) {}
}
