package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark;

import java.util.List;

public interface BookmarkQueryUsecase {

    List<BookmarkedPostSearchResult> getBookmarkedPosts(Long memberId, int page, int size);

    record BookmarkedPostSearchResult(
            Long id, String title, String imageUrl, String nickname,
            int likeCount, int commentCount, boolean liked
    ) {}
}
