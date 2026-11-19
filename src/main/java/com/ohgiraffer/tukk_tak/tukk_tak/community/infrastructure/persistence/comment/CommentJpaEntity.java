package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.comment;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Comment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public CommentJpaEntity(Long id, Long postId, Long memberId, Long parentId, String content,
                            boolean isPrivate, boolean isDeleted,
                            LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.parentId = parentId;
        this.content = content;
        this.isPrivate = isPrivate;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static CommentJpaEntity fromDomain(Comment comment) {
        return new CommentJpaEntity(
                comment.getId(),
                comment.getPostId(),
                comment.getMemberId(),
                comment.getParentId(),
                comment.getContent(),
                comment.isPrivate(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getDeletedAt()
        );
    }
}
