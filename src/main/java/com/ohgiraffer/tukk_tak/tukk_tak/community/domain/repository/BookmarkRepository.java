package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;

import java.util.List;


public interface BookmarkRepository {
    boolean exists(Long postId, Long memberId);

    void delete(Long postId, Long memberId);

    void save(Bookmark bookmark);

    List<BookmarkedPostInfo> findBookmarkedPosts(Long memberId, int page, int size);

    record BookmarkedPostInfo(
            Long id, String title, String imageUrl, String nickname,
            int likeCount, int commentCount, boolean liked
    ) {}
}
