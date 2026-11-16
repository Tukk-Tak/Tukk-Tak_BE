package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Category;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 100)
    private String tags;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public PostJpaEntity(Long id, Long memberId, Category category, String title, String content, String imageUrl, String tags, int likeCount, int commentCount, boolean deleted, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.memberId = memberId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static PostJpaEntity fromDomain(Post post) {
        return new PostJpaEntity(
                post.getId(),
                post.getMemberId(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getTags().isEmpty() ? null : String.join(",", post.getTags()),
                post.getLikeCount(),
                post.getCommentCount(),
                post.isDeleted(),
                post.getCreatedAt(),
                post.getDeletedAt()
        );
    }
}