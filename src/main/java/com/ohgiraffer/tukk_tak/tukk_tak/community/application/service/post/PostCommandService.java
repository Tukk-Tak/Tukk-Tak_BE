package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreatePostCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.policy.PostCreatePolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.post.PostRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.global.s3.S3FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService implements PostCommandUsecase {

    private final PostRepository postRepository;
    private final PostCreatePolicy postCreatePolicy;
    private final S3FileStorage s3FileStorage;

    @Override
    public void createPost(CreatePostCommand createPostCommand) {

        // policy로 검증
        postCreatePolicy.validate(
                createPostCommand.title(),
                createPostCommand.content(),
                createPostCommand.tags(),
                createPostCommand.image()
        );

        // s3에 올리고
        String imageKey = s3FileStorage.upload(createPostCommand.image(), "community");

        try {
            postRepository.save(Post.create(
                    createPostCommand.memberId(),
                    createPostCommand.title(),
                    createPostCommand.content(),
                    imageKey,
                    createPostCommand.tags()
            ));
        } catch (RuntimeException e) {
            s3FileStorage.delete(imageKey);   // 저장이 실패하면 올린 이미지를 지움
            throw e;
        }
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        post.delete(memberId);
        postRepository.save(post);

    }

}