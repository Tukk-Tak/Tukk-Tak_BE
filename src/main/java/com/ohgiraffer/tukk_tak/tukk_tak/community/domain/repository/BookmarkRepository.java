package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;

import java.util.List;


public interface BookmarkRepository {
    boolean exists(Long postId, Long memberId);

    int delete(Long postId, Long memberId);

    void save(Bookmark bookmark);

    List<BookmarkedPostInfo> findBookmarkedPosts(Long memberId, int page, int size);

    boolean findBookmarked(Long memberId, Long postId);

    long countBookmarkedPosts(Long memberId);

    record BookmarkedPostInfo(
            Long id, Long memberId,String title, String imageUrl,
            int likeCount, int commentCount, boolean liked
    ) {}
}
