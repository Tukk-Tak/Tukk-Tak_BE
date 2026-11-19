package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Like;

public interface LikeRepository {
    boolean exists(Long postId, Long memberId);

    int delete(Long postId, Long memberId);

    void save(Like like);

    boolean findLiked(Long memberId, Long postId);
}
