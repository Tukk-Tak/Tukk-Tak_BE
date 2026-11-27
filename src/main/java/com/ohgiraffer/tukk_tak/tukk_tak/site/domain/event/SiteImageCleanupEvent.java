package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.event;

import java.util.Set;

public record SiteImageCleanupEvent(
        Set<String> keys) {
}
