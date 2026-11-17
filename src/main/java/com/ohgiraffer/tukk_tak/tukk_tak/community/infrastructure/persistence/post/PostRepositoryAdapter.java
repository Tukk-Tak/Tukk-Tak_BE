package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepository {

    private final SpringDataPostRepository springDataPostRepository;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public List<PostInfo> findPostInfo(String keyword, Sort sort, int page, int size) {
        String trimmed = (keyword == null) ? "" : keyword.trim();

        // '#'으로 시작하면 태그만 검색 아니면 통합 검색
        boolean tagOnly = trimmed.startsWith("#");
        String searchKeyword = tagOnly ? trimmed.substring(1).trim() : trimmed;

        // 비었거나 공백이면 전체 조회
        if (searchKeyword.isEmpty()) {
            searchKeyword = null;
            tagOnly = false;
        }

        int offset = (page - 1) * size;

        List<SpringDataPostRepository.PostInfoProjection> rows = (sort == Sort.POPULAR)
                ? springDataPostRepository.findPostInfosPopular(searchKeyword, tagOnly, size, offset)
                : springDataPostRepository.findPostInfosLatest(searchKeyword, tagOnly, size, offset);

        return rows.stream()
                .map(p -> new PostInfo(
                        p.getId(), p.getTitle(), toImageUrl(p.getImageUrl()), p.getNickname(),
                        splitTags(p.getTags()),
                        p.getLikeCount(), p.getCommentCount()))
                .toList();
    }

    @Override
    public Set<Long> findLikedPostIds(Long memberId, List<Long> postIds) {
        return new HashSet<>(springDataPostRepository.findLikedPostIds(memberId, postIds));
    }

    @Override
    public Set<Long> findBookmarkedPostIds(Long memberId, List<Long> postIds) {
        return new HashSet<>(springDataPostRepository.findBookmarkedPostIds(memberId, postIds));
    }

    @Override
    public Long save(Post post) {
        return springDataPostRepository.save(PostJpaEntity.fromDomain(post)).getId();
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return springDataPostRepository.findByIdAndDeletedFalse(postId)
                .map(PostJpaEntity::toDomain);
    }

    @Override
    public void minusLikeCount(Long postId) {
        springDataPostRepository.minusLikeCount(postId);
    }

    @Override
    public void plusLikeCount(Long postId) {
        springDataPostRepository.plusLikeCount(postId);
    }

    // CloudFront 도메인을 붙여 URL로
    private String toImageUrl(String key) {
        return (key == null || key.isBlank()) ? null : cloudFrontDomain + "/" + key;
    }

    // tags는 콤마로 구분되어 있어서 콤마 기준으로 쪼개고 목록으로 변환
    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }
}