package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Like {
    private final Long id;
    private final Long postId;
    private final Long memberId;
    private final LocalDateTime createdAt;

    @Builder
    private Like(Long id, Long postId, Long memberId, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    public static Like create(Long postId, Long memberId) {
        return Like.builder()
                .postId(postId)
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}