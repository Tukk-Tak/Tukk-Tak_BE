package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static BookmarkJpaEntity fromDomain(Bookmark bookmark) {
        BookmarkJpaEntity entity = new BookmarkJpaEntity();
        entity.postId = bookmark.getPostId();
        entity.memberId = bookmark.getMemberId();
        entity.createdAt = bookmark.getCreatedAt();
        return entity;
    }
}
