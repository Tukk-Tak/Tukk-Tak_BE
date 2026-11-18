package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.like;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Like;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "post_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static PostLikeJpaEntity fromDomain(Like like) {
        PostLikeJpaEntity entity = new PostLikeJpaEntity();
        entity.postId = like.getPostId();
        entity.memberId = like.getMemberId();
        entity.createdAt = like.getCreatedAt();
        return entity;
    }
}
