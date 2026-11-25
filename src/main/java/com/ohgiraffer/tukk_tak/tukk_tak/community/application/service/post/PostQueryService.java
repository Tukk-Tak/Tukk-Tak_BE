package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.port.GetNicknamePort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.LikeRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository.PostInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository.PostDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService implements PostQueryUsecase {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final GetNicknamePort getNicknamePort;

    @Override
    public PostListResult getPosts(Long memberId, String keyword, Sort sort, int page, int size) {
        Sort orderBy = sort != null ? sort : (memberId == null ? Sort.POPULAR : Sort.LATEST);

        long totalCount = postRepository.countPosts(keyword);
        int totalPages = (int) ((totalCount + size - 1) / size);

        List<PostInfo> posts = postRepository.findPostInfo(keyword, orderBy, page, size);
        if (posts.isEmpty()) {
            return new PostListResult(List.of(), page, size, totalCount, totalPages);
        }

        List<Long> postIds = posts.stream().map(PostInfo::id).toList();

        // 유저 id가 있다면(=로그인) 좋아요/북마크 누른 거 조회
        Set<Long> likedIds = memberId == null
                ? Set.of() : postRepository.findLikedPostIds(memberId, postIds);
        Set<Long> bookmarkedIds = memberId == null
                ? Set.of() : postRepository.findBookmarkedPostIds(memberId, postIds);

        Map<Long, String> nicknames = getNicknamePort.getNicknames(
                posts.stream().map(PostInfo::memberId).collect(Collectors.toSet()));

        List<PostSearchResult> results = posts.stream()
                .map(p -> new PostSearchResult(
                        p.id(), p.title(), p.imageUrl(), nicknames.get(p.memberId()), p.tags(),
                        p.likeCount(), p.commentCount(),
                        likedIds.contains(p.id()), bookmarkedIds.contains(p.id())))
                .toList();

        return new PostListResult(results, page, size, totalCount, totalPages);
    }

    @Override
    public PostDetailSearchResult getPostDetail(Long memberId, Long postId) {

        PostDetail postDetail = postRepository.getPostDetail(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        // 유저 id가 있다면(=로그인) 좋아요/북마크 누른 거 조회
        // id가 널이 아니고 존재여부가 참일 때만 참
        boolean liked = memberId != null && likeRepository.findLiked(memberId, postId);
        boolean bookmarked = memberId != null && bookmarkRepository.findBookmarked(memberId, postId);

        return  new PostDetailSearchResult(
                postDetail.id(),postDetail.title(),postDetail.imageUrl(),getNicknamePort.getNickname(postDetail.memberId()) ,
                postDetail.content(), postDetail.tags(),postDetail.createAt(), postDetail.likeCount(), postDetail.commentCount(),
                liked,bookmarked
                );

    }
}
