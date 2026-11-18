package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.bookemark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataBookmarkRepository extends JpaRepository<BookmarkJpaEntity,Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("DELETE FROM BookmarkJpaEntity b WHERE b.postId = :postId AND b.memberId = :memberId")
    int deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Query(value = """
        SELECT p.id            AS id,
               p.title         AS title,
               p.image_url     AS imageUrl,
               m.nickname      AS nickname,
               p.like_count    AS likeCount,
               p.comment_count AS commentCount,
               CASE WHEN l.id IS NULL THEN 0 ELSE 1 END AS liked
        FROM bookmark b
        JOIN post p ON p.id = b.post_id AND p.is_deleted = FALSE
        JOIN member m ON m.id = p.member_id
        LEFT JOIN post_like l ON l.post_id = p.id AND l.member_id = b.member_id
        WHERE b.member_id = :memberId
        ORDER BY b.created_at DESC, b.id DESC
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<BookmarkedPostInfoProjection> findBookmarkedPostInfos(@Param("memberId") Long memberId,
                                                               @Param("size") int size,
                                                               @Param("offset") int offset);

    interface BookmarkedPostInfoProjection {
        Long getId();
        String getTitle();
        String getImageUrl();
        String getNickname();
        int getLikeCount();
        int getCommentCount();
        int getLiked();
    }
}
