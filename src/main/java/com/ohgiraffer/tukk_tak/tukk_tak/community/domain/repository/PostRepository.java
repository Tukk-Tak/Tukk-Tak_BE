package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository {

    List<PostInfo> findPostInfo(String keyword, Sort sort, int page, int size);

    long countPosts(String keyword);

    // 로그인 한 사용자가 게시글 목록 조회할 때 자신이 좋아요 누른 게시글 표시용
    Set<Long> findLikedPostIds(Long memberId, List<Long> postIds);

    // 로그인 한 사용자가 게시글 목록 조회할 때 자신이 북마크 누른 게시글 표시용
    Set<Long> findBookmarkedPostIds(Long memberId, List<Long> postIds);

    Long save(Post post);

    Optional<Post> findById(Long postId);

    void minusLikeCount(Long postId);

    void plusLikeCount(Long postId);

    Optional<PostDetail> getPostDetail(Long postId);

    record PostInfo(
            Long id,  Long memberId, String title, String imageUrl,
            List<String> tags, int likeCount, int commentCount
    ) {}
    
    record PostDetail(
            Long id, Long memberId, String title, String imageUrl, String content,
            List<String> tags, LocalDateTime createAt, int likeCount, int commentCount
    ){}
}