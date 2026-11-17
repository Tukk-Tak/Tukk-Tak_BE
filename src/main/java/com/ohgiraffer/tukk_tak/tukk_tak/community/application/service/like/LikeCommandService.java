package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.like;

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

    public final LikeRepository likeRepository;
    public final PostRepository postRepository;

    @Override
    public boolean like(Long postId, Long memberId) {

         postRepository.findById(postId)
                // 게시글로 조회는 되지만 소프트 딜리트 처리된 애들도 제외
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        if (likeRepository.exists(postId, memberId)) {
            if (likeRepository.delete(postId, memberId) == 1) {
                postRepository.minusLikeCount(postId);
            }
            return false;
        }

        likeRepository.save(Like.create(postId, memberId));
        postRepository.plusLikeCount(postId);
        return true;
    }
}
