package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.SiteForbiddenException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Site {

    private final Long id;
    private final Long memberId;
    private String title;
    private final SiteCategory category;
    private SiteStatus status;
    private final String slug;
    private LocalDateTime lastSavedAt;
    private final LocalDateTime publishedAt;
    private final LocalDateTime createdAt;

    @Builder
    private Site(Long id, Long memberId, String title, SiteCategory category, SiteStatus status,
                 String slug, LocalDateTime lastSavedAt, LocalDateTime publishedAt,
                 LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.category = category;
        this.status = status;
        this.slug = slug;
        this.lastSavedAt = lastSavedAt;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
    }

    // 최초 저장: DRAFT 상태로 생성
    public static Site create(Long memberId, String title) {
        LocalDateTime now = LocalDateTime.now();
        return Site.builder()
                .memberId(memberId)
                .title(title)
                .category(SiteCategory.RESTAURANT)
                .status(SiteStatus.DRAFT)
                .lastSavedAt(now)
                .createdAt(now)
                .build();
    }

    // 수정 저장: 본인 사이트만 가능
    public void update(Long requesterId, String title) {
        validateOwner(requesterId);
        this.title = title;
        this.lastSavedAt = LocalDateTime.now();
    }

    public void validateOwner(Long requesterId) {
        if (!memberId.equals(requesterId)) {
            throw new SiteForbiddenException("본인의 사이트만 이용할 수 있어요.");
        }
    }

}
