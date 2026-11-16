package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Post {
    private final Long id;
    private final Long memberId;
    private final Category category;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final List<String> tags;
    private final int likeCount;
    private final int commentCount;
    private final boolean deleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder
    private Post(Long id, Long memberId, Category category, String title, String content,
                 String imageUrl, List<String> tags, int likeCount, int commentCount,
                 boolean deleted, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.memberId = memberId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.tags = tags == null ? List.of() : List.copyOf(tags);
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static Post create(Long memberId, String title, String content, String imageKey, List<String> tags) {
        return Post.builder()
                .memberId(memberId)
                .category(Category.STORE_INTRO)
                .title(title)
                .content(content)
                .imageUrl(imageKey)
                .tags(tags)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
