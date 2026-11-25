package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.like;

public interface LikeCommandUsecase {

    boolean like(Long postId, Long memberId);
}
