package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.event;

public record SiteDeletedEvent(
        Long siteId,
        String slug
) {
}
