package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post;


import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface PostQueryUsecase {
    PostListResult getPosts(Long memberId, String keyword, Sort sort, int page, int size);

    PostDetailSearchResult getPostDetail(Long memberId, Long postId);

    record PostListResult(
            List<PostSearchResult> posts,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    record PostSearchResult(
            Long id, String title, String imageUrl, String nickname,
            List<String> tags, int likeCount, int commentCount,
            boolean liked, boolean bookmarked
    ) {}

    record PostDetailSearchResult(
            Long id, String title, String imageUrl, String nickname, String content,
            List<String> tags, LocalDateTime createdAt, int likeCount, int commentCount,
            boolean liked, boolean bookmarked
    ) {}
}
