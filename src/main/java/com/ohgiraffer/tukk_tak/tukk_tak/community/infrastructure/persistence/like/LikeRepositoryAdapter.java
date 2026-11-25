package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.like;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.LikeAlreadyExistsException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Like;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryAdapter implements LikeRepository {

    private final SpringDataLikeRepository springDataLikeRepository;

    @Override
    public boolean exists(Long postId, Long memberId) {
        return springDataLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public int delete(Long postId, Long memberId) {
        return springDataLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public void save(Like like) {
        try {
            springDataLikeRepository.save(PostLikeJpaEntity.fromDomain(like));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("uq_post_like")) {
                throw new LikeAlreadyExistsException("좋아요 요청이 겹쳤어요. 잠시 후 다시 시도해주세요.");
            }
            throw e;
        }
    }

    @Override
    public boolean findLiked(Long memberId, Long postId) {
        return springDataLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}
