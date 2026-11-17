package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.post.PostRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.post.PostRepository.PostInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService implements PostQueryUsecase {

    private final PostRepository postRepository;

    @Override
    public List<PostSearchResult> getPosts(Long memberId, String keyword, Sort sort, int page, int size) {
        Sort orderBy = sort != null ? sort : (memberId == null ? Sort.POPULAR : Sort.LATEST);

        List<PostInfo> posts = postRepository.findPostInfo(keyword, orderBy, page, size);
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(PostInfo::id).toList();

        // 유저 id가 있다면(=로그인) 좋아요/북마크 누른 거 조회
        Set<Long> likedIds = memberId == null
                ? Set.of() : postRepository.findLikedPostIds(memberId, postIds);
        Set<Long> bookmarkedIds = memberId == null
                ? Set.of() : postRepository.findBookmarkedPostIds(memberId, postIds);

        return posts.stream()
                .map(p -> new PostSearchResult(
                        p.id(), p.title(), p.imageUrl(), p.nickname(), p.tags(),
                        p.likeCount(), p.commentCount(),
                        likedIds.contains(p.id()), bookmarkedIds.contains(p.id())))
                .toList();
    }
}
