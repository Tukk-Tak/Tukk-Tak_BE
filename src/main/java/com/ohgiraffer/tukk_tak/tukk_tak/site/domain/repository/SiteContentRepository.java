package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository;

import java.util.List;
import java.util.Map;

public interface SiteContentRepository {

    // 없으면 만들고 있으면 theme와 rows를 통째로 교체
    void save(Long siteId, Map<String, Object> theme, List<Map<String, Object>> rows);
}
