package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPostRepository  extends JpaRepository<PostJpaEntity, Long> {

    // 최신순 정렬
    @Query(value = """
    SELECT p.id            AS id,
           p.member_id     AS memberId,
           p.title         AS title,
           p.image_url     AS imageUrl,
           p.tags          AS tags,
           p.like_count    AS likeCount,
           p.comment_count AS commentCount
    FROM post p
    JOIN member m ON m.id = p.member_id
    WHERE p.is_deleted = FALSE
      AND (
            :keyword IS NULL
            OR (:tagOnly = TRUE AND p.tags LIKE CONCAT('%', :keyword, '%'))
            OR (:tagOnly = FALSE AND (
                   m.nickname LIKE CONCAT('%', :keyword, '%')
                OR p.title    LIKE CONCAT('%', :keyword, '%')
                OR p.tags     LIKE CONCAT('%', :keyword, '%')))
          )
    ORDER BY p.id DESC
    LIMIT :size OFFSET :offset
    """, nativeQuery = true)
    List<PostInfoProjection> findPostInfosLatest(@Param("keyword") String keyword,
                                                 @Param("tagOnly") boolean tagOnly,
                                                 @Param("size") int size,
                                                 @Param("offset") int offset);

    // 인기순 정렬
    @Query(value = """
    SELECT p.id            AS id,
           p.member_id     AS memberId,
           p.title         AS title,
           p.image_url     AS imageUrl,
           p.tags          AS tags,
           p.like_count    AS likeCount,
           p.comment_count AS commentCount
    FROM post p
    JOIN member m ON m.id = p.member_id
    WHERE p.is_deleted = FALSE
      AND (
            :keyword IS NULL
            OR (:tagOnly = TRUE AND p.tags LIKE CONCAT('%', :keyword, '%'))
            OR (:tagOnly = FALSE AND (
                   m.nickname LIKE CONCAT('%', :keyword, '%')
                OR p.title    LIKE CONCAT('%', :keyword, '%')
                OR p.tags     LIKE CONCAT('%', :keyword, '%')))
          )
    ORDER BY p.like_count DESC, p.id DESC
    LIMIT :size OFFSET :offset
    """, nativeQuery = true)
    List<PostInfoProjection> findPostInfosPopular(@Param("keyword") String keyword,
                                                  @Param("tagOnly") boolean tagOnly,
                                                  @Param("size") int size,
                                                  @Param("offset") int offset);


    // 검색 조건에 맞는 게시글 총 개수
    @Query(value = """
    SELECT COUNT(*)
    FROM post p
    JOIN member m ON m.id = p.member_id
    WHERE p.is_deleted = FALSE
      AND (
            :keyword IS NULL
            OR (:tagOnly = TRUE AND p.tags LIKE CONCAT('%', :keyword, '%'))
            OR (:tagOnly = FALSE AND (
                   m.nickname LIKE CONCAT('%', :keyword, '%')
                OR p.title    LIKE CONCAT('%', :keyword, '%')
                OR p.tags     LIKE CONCAT('%', :keyword, '%')))
          )
    """, nativeQuery = true)
    long countPosts(@Param("keyword") String keyword,
                        @Param("tagOnly") boolean tagOnly);

    // 좋아요 누른 게시글 조회
    @Query(value = """
            SELECT post_id FROM post_like
            WHERE member_id = :memberId AND post_id IN (:postIds)
            """, nativeQuery = true)
    List<Long> findLikedPostIds(@Param("memberId") Long memberId,
                                @Param("postIds") List<Long> postIds);

    // 북마크 누른 게시글 조회
    @Query(value = """
            SELECT post_id FROM bookmark
            WHERE member_id = :memberId AND post_id IN (:postIds)
            """, nativeQuery = true)
    List<Long> findBookmarkedPostIds(@Param("memberId") Long memberId,
                                     @Param("postIds") List<Long> postIds);

    Optional<PostJpaEntity> findByIdAndDeletedFalse(Long postId);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void minusLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void plusLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void plusCommentCount(@Param("postId") Long postId);

    interface PostInfoProjection {
        Long getId();
        Long getMemberId();
        String getTitle();
        String getImageUrl();
        String getTags();
        int getLikeCount();
        int getCommentCount();
    }
}