package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkQueryUsecase;

public record GetBookmarkList(
        Long id,
        String title,
        String imageUrl,
        String nickname,
        int likeCount,
        int commentCount,
        boolean liked
) {

    public static GetBookmarkList from(BookmarkQueryUsecase.BookmarkedPostSearchResult r) {
        return new GetBookmarkList(r.id(), r.title(), r.imageUrl(), r.nickname(),
                r.likeCount(), r.commentCount(),r.liked());
    }
}
