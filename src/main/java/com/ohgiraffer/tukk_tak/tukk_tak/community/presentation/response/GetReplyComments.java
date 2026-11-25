package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment.CommentQueryUsecase;

import java.time.LocalDateTime;

public record GetReplyComments(
        Long id,
        String nickname,
        String content,
        LocalDateTime createdAt,
        boolean isPrivate,
        boolean isDeleted,
        boolean isWriter,
        boolean isMine
) {

    public static GetReplyComments from(CommentQueryUsecase.ReplyCommentSearchResult r) {
        return new GetReplyComments(r.id(), r.nickname(), r.content(), r.createdAt(),
                r.isPrivate(), r.isDeleted(),  r.isWriter(), r.isMine());
    }
}
