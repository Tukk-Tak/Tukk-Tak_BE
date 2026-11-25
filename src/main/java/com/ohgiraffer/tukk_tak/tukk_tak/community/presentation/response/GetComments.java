package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment.CommentQueryUsecase;

import java.time.LocalDateTime;
import java.util.List;

public record GetComments(
        List<Comment> comments,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static GetComments from(CommentQueryUsecase.CommentListResult r) {
        return new GetComments(
                r.comments().stream().map(Comment::from).toList(),
                r.page(), r.size(), r.totalElements(), r.totalPages());
    }

    public record Comment(
            Long id,
            String nickname,
            String content,
            LocalDateTime createdAt,
            Long replyCount,
            boolean isPrivate,
            boolean isDeleted,
            boolean isWriter,
            boolean isMine
    ) {
        static Comment from(CommentQueryUsecase.CommentSearchResult r) {
            return new Comment(r.id(), r.nickname(), r.content(), r.createdAt(), r.replyCount(),
                    r.isPrivate(), r.isDeleted(), r.isWriter(), r.isMine());
        }
    }
}