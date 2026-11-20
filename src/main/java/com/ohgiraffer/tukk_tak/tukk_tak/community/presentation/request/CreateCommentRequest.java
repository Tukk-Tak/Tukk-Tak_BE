package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(

        Long parentCommentId,

        @NotBlank(message = "댓글을 입력해주세요.")
        @Size(max = 100,  message = "댓글은 최대 100글자까지 입력할 수 있어요.")
        String content,

        boolean isPrivate
) {
}
