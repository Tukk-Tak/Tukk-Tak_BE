package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.like;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.event.LikeNotificationPublisher;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.like.LikeCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Like;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.LikeRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeCommandService implements LikeCommandUsecase{

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final LikeNotificationPublisher likeNotificationPublisher;

    @Override
    public boolean like(Long postId, Long memberId) {

         Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        if (likeRepository.exists(postId, memberId)) {
            if (likeRepository.delete(postId, memberId) == 1) {
                postRepository.minusLikeCount(postId);
            return false;
            }
        }

        postRepository.plusLikeCount(postId);
        likeRepository.save(Like.create(postId, memberId));
        likeNotificationPublisher.publish(post, memberId);
        return true;
    }
}
