package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark;

public interface BookmarkCommandUsecase {
    boolean bookmark(Long postId, Long memberId);
}
