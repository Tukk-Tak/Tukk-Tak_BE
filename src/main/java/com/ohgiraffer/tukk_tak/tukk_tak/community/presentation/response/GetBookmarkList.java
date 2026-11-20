package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkQueryUsecase;

import java.util.List;

public record GetBookmarkList(
        List<BookmarkedPosts> bookmarks,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static GetBookmarkList from(BookmarkQueryUsecase.BookmarkListResult r) {
        return new GetBookmarkList(
                r.bookmarks().stream().map(BookmarkedPosts::from).toList(),
                r.page(), r.size(), r.totalElements(), r.totalPages());
    }

    public record BookmarkedPosts(
            Long id,
            String title,
            String imageUrl,
            String nickname,
            int likeCount,
            int commentCount,
            boolean liked
    ) {
        static BookmarkedPosts from(BookmarkQueryUsecase.BookmarkedPostSearchResult r) {
            return new BookmarkedPosts(r.id(), r.title(), r.imageUrl(), r.nickname(),
                    r.likeCount(), r.commentCount(), r.liked());
        }
    }
}