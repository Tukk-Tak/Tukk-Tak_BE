package com.ohgiraffer.tukk_tak.tukk_tak.community.application.command;

public record CreateCommentCommand(
        Long memberId,
        Long postId,
        Long parentCommentId,
        String content,
        boolean isPrivate
) {
}
