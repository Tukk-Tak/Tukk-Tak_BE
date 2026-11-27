package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteCategory;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "site")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SiteCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SiteStatus status;

    @Column(length = 30)
    private String slug;

    @Column(name = "last_saved_at")
    private LocalDateTime lastSavedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static SiteJpaEntity fromDomain(Site site) {
        SiteJpaEntity entity = new SiteJpaEntity();
        entity.id = site.getId();
        entity.memberId = site.getMemberId();
        entity.title = site.getTitle();
        entity.category = site.getCategory();
        entity.status = site.getStatus();
        entity.slug = site.getSlug();
        entity.lastSavedAt = site.getLastSavedAt();
        entity.publishedAt = site.getPublishedAt();
        entity.createdAt = site.getCreatedAt();
        return entity;
    }

    public Site toDomain() {
        return Site.builder()
                .id(id)
                .memberId(memberId)
                .title(title)
                .category(category)
                .status(status)
                .slug(slug)
                .lastSavedAt(lastSavedAt)
                .publishedAt(publishedAt)
                .createdAt(createdAt)
                .build();
    }

}
