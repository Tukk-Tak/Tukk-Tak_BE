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
            // 데이터 정합성 관련 에러를 잡아서 전부 동일 에러 메시지로 던지니까
            // 유니크 제약 위반이 아닌 다른 정합성 문제여도 동일 메시지로 나감 유의
            throw new LikeAlreadyExistsException("좋아요 요청이 겹쳤어요. 잠시 후 다시 시도해주세요.");
        }
    }
}
