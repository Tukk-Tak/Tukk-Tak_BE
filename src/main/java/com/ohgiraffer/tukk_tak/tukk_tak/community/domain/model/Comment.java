package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {

    private final Long id;
    private final Long postId;
    private final Long memberId;
    private final Long parentId;
    private final String content;
    private final boolean isPrivate;
    private final boolean isDeleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder
    public Comment(Long id, Long postId, Long memberId, Long parentId, String content, boolean isPrivate, boolean isDeleted, LocalDateTime createdAt, LocalDateTime deletedAt) {
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
}
