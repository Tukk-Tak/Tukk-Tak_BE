package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.BookmarkAlreadyExistsException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookmarkRepositoryAdapter implements BookmarkRepository {

    private final SpringDataBookmarkRepository springDataBookmarkRepository;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public boolean exists(Long postId, Long memberId) {
        return springDataBookmarkRepository.existsByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public int delete(Long postId, Long memberId) {
        return springDataBookmarkRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public void save(Bookmark bookmark) {
        try {
            springDataBookmarkRepository.save(BookmarkJpaEntity.fromDomain(bookmark));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("uq_bookmark")) {
                throw new BookmarkAlreadyExistsException("북마크 요청이 겹쳤어요. 잠시 후 다시 시도해주세요.");
            }
            throw e;
        }
    }

    @Override
    public List<BookmarkedPostInfo> findBookmarkedPosts(Long memberId, int page, int size) {
        int offset = (page - 1) * size;

        return springDataBookmarkRepository.findBookmarkedPostInfos(memberId, size, offset).stream()
                .map(p -> new BookmarkedPostInfo(
                        p.getId(), p.getTitle(), toImageUrl(p.getImageUrl()), p.getNickname(),
                        p.getLikeCount(), p.getCommentCount(), p.getLiked() == 1))
                .toList();
    }

    // CloudFront 도메인 붙여서 URL로
    private String toImageUrl(String key) {
        return (key == null || key.isBlank()) ? null : cloudFrontDomain + "/" + key;
    }
}
