package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;

import java.util.List;

public record GetPostList(
        Long id,
        String title,
        String imageUrl,
        String nickname,
        List<String> tags,
        int likeCount,
        int commentCount,
        boolean liked,        // 비로그인이면 항상 false
        boolean bookmarked    // 비로그인이면 항상 false
) {
    public static GetPostList from(PostQueryUsecase.PostSearchResult r) {
        return new GetPostList(r.id(), r.title(), r.imageUrl(), r.nickname(),
                r.tags(), r.likeCount(), r.commentCount(), r.liked(), r.bookmarked());
    }
}
