package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataCommentRepository extends JpaRepository<CommentJpaEntity,Long> {

    // 부모 댓글만, 최신순. 삭제된 댓글은 삭제 안 된 답글이 하나라도 있을 때만 남김
    @Query("""
            SELECT c.id AS id, c.memberId AS memberId, c.content AS content,
                   c.createdAt AS createdAt, c.isPrivate AS isPrivate, c.isDeleted AS isDeleted
            FROM CommentJpaEntity c
            WHERE c.postId = :postId
              AND c.parentId IS NULL
              AND (c.isDeleted = false
                   OR EXISTS (SELECT 1 FROM CommentJpaEntity r
                              WHERE r.parentId = c.id AND r.isDeleted = false))
            ORDER BY c.createdAt DESC, c.id DESC
            """)
    List<CommentInfoProjection> findComments(@Param("postId") Long postId, Pageable pageable);


    // 부모 댓글별 답글 수 - 삭제 안 된 답글만
    @Query("""
            SELECT c.parentId AS parentId, COUNT(c) AS replyCount
            FROM CommentJpaEntity c
            WHERE c.parentId IN :parentIds AND c.isDeleted = false
            GROUP BY c.parentId
            """)
    List<ReplyCountProjection> countRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

    // 부모 댓글별 답글 수 - 삭제된 답글 포함 (대댓글 목록과 동일 기준)
//    @Query("""
//            SELECT c.parentId AS parentId, COUNT(c) AS replyCount
//            FROM CommentJpaEntity c
//            WHERE c.parentId IN :parentIds
//            GROUP BY c.parentId
//            """)
//    List<ReplyCountProjection> countRepliesByParentIds(@Param("parentIds") List<Long> parentIds);


    @Query("""
        SELECT COUNT(c)
        FROM CommentJpaEntity c
        WHERE c.postId = :postId
          AND c.parentId IS NULL
          AND (c.isDeleted = false
               OR EXISTS (SELECT 1 FROM CommentJpaEntity r
                          WHERE r.parentId = c.id AND r.isDeleted = false))
        """)
    long countComments(@Param("postId") Long postId);

    // 부모 댓글 단건: 이 게시글 소속이고 대댓글이 아닌 것만 (삭제된 부모도 조회됨)
    Optional<CommentInfoProjection> findByIdAndPostIdAndParentIdIsNull(Long id, Long postId);

    // 대댓글 목록: 삭제되지 않은 것만, 오래된 순
    List<CommentInfoProjection> findByParentIdOrderByCreatedAtAscIdAsc(Long parentId);

    @Modifying
    @Query("UPDATE CommentJpaEntity c SET c.isDeleted = true, c.deletedAt = :deletedAt WHERE c.id = :commentId AND c.isDeleted = false")
    int isDeleted(@Param("commentId") Long commentId, @Param("deletedAt") LocalDateTime deletedAt);

    interface CommentInfoProjection {
        Long getId();
        Long getMemberId();
        String getContent();
        LocalDateTime getCreatedAt();
        boolean getIsPrivate();
        boolean getIsDeleted();
    }

    interface ReplyCountProjection {
        Long getParentId();
        Long getReplyCount();
    }
}