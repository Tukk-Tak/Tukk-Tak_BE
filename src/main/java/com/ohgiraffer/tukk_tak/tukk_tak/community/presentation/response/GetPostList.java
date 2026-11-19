package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;

import java.util.List;

public record GetPostList(
        List<Item> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static GetPostList from(PostQueryUsecase.PostListResult result) {
        return new GetPostList(
                result.posts().stream().map(Item::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public record Item(
            Long id,
            String title,
            String imageUrl,
            String nickname,
            List<String> tags,
            int likeCount,
            int commentCount,
            boolean liked, // 비로그인이면 항상 false
            boolean bookmarked // 비로그인이면 항상 false
    ) {
        static Item from(PostQueryUsecase.PostSearchResult r) {
            return new Item(
                    r.id(), r.title(), r.imageUrl(), r.nickname(), r.tags(),
                    r.likeCount(), r.commentCount(), r.liked(), r.bookmarked()
            );
        }
    }
}