package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SiteContentRepository {

    // 없으면 만들고 있으면 theme와 rows를 통째로 교체
    void save(Long siteId, Map<String, Object> theme, List<Map<String, Object>> rows);

    void deleteBySiteId(Long siteId);

    Map<Long, SiteContent> findAllBySiteIdIn(List<Long> siteIds);

    default Optional<SiteContent> findBySiteId(Long siteId) {
        return Optional.ofNullable(findAllBySiteIdIn(List.of(siteId)).get(siteId));
    }

    record SiteContent(
            Map<String, Object> theme,
            List<Map<String, Object>> rows
    ) {}
}
