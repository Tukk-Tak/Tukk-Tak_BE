package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Bookmark {

    private final Long id;
    private final Long postId;
    private final Long memberId;
    private final LocalDateTime createdAt;

    @Builder
    private Bookmark(Long id, Long postId, Long memberId, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    public static Bookmark create(Long postId, Long memberId) {
        return Bookmark.builder()
                .postId(postId)
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
