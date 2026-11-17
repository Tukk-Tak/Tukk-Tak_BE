package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreatePostCommand;

public interface PostCommandUsecase {
    Long createPost(CreatePostCommand createPostCommand);

    void deletePost(Long postId, Long memberId);
}
