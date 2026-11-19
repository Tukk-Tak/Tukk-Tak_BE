package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark;

import java.util.List;

public interface BookmarkQueryUsecase {

    BookmarkListResult getBookmarkedPosts(Long memberId, int page, int size);

    record BookmarkListResult(
            List<BookmarkedPostSearchResult> bookmarks, int page, int size,
            long totalElements, int totalPages
    ) {}

    record BookmarkedPostSearchResult(
            Long id, String title, String imageUrl, String nickname,
            int likeCount, int commentCount, boolean liked
    ) {}
}
