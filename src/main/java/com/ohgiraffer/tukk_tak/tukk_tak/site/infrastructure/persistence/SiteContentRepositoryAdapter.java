package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SiteContentRepositoryAdapter implements SiteContentRepository {

    private final SpringDataSiteContentRepository springDataSiteContentRepository;

    @Override
    public void save(Long siteId, Map<String, Object> theme, List<Map<String, Object>> rows) {
        SiteContentDocument document = springDataSiteContentRepository.findBySiteId(siteId)
                .map(existing -> {
                    existing.update(theme, rows);
                    return existing;
                })
                .orElseGet(() -> SiteContentDocument.of(siteId, theme, rows));

        springDataSiteContentRepository.save(document);
    }
}