package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;

public interface BookmarkRepository {
    boolean exists(Long postId, Long memberId);

    void delete(Long postId, Long memberId);

    void save(Bookmark bookmark);
}
