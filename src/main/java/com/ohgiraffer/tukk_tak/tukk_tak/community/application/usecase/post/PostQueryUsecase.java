package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post;


import java.util.List;

public interface PostQueryUsecase {
    List<PostSearchResult> getPosts(String keyword, int page, int size);

    record PostSearchResult(
            Long id, String title, String imageUrl, String nickname,
            List<String> tags, int likeCount, int commentCount,
            boolean liked, boolean bookmarked
    ) {}
}
