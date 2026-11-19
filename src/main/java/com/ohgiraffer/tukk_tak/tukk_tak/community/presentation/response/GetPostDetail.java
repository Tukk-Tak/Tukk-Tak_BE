package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;

import java.time.LocalDateTime;
import java.util.List;

public record GetPostDetail(
        Long id,
        String title,
        String imageUrl,
        String nickname,
        String content,
        List<String> tags,
        LocalDateTime createdAt,
        int likeCount,
        int commentCount,
        boolean liked,        // 비로그인이면 항상 false
        boolean bookmarked    // 비로그인이면 항상 false
) {

    public static GetPostDetail from(PostQueryUsecase.PostDetailSearchResult r) {
        return new GetPostDetail(r.id(), r.title(), r.imageUrl(), r.nickname(),r.content(),
                r.tags(), r.createdAt(), r.likeCount(), r.commentCount(),r.liked(), r.bookmarked());
    }
}
