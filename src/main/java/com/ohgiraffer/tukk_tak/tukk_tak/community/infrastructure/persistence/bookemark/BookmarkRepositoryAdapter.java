package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.BookmarkAlreadyExistsException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookmarkRepositoryAdapter implements BookmarkRepository {

    private final SpringDataBookmarkRepository springDataBookmarkRepository;

    @Override
    public boolean exists(Long postId, Long memberId) {
        return springDataBookmarkRepository.existsByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public void delete(Long postId, Long memberId) {
        springDataBookmarkRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public void save(Bookmark bookmark) {
        try {
            springDataBookmarkRepository.save(BookmarkJpaEntity.fromDomain(bookmark));
        } catch (DataIntegrityViolationException e) {
            // 데이터 정합성 관련 에러를 잡아서 전부 동일 에러 메시지로 던지니까
            // 유니크 제약 위반이 아닌 다른 정합성 문제여도 동일 메시지로 나감 유의
            throw new BookmarkAlreadyExistsException("북마크 요청이 겹쳤어요. 잠시 후 다시 시도해주세요.");
        }
    }
}
