package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post;


import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;

import java.util.List;

public interface PostQueryUsecase {
    List<PostSearchResult> getPosts(Long memberId, String keyword, Sort sort, int page, int size);

    record PostSearchResult(
            Long id, String title, String imageUrl, String nickname,
            List<String> tags, int likeCount, int commentCount,
            boolean liked, boolean bookmarked
    ) {}
}
