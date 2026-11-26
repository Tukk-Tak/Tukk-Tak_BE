package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;

import java.util.List;
import java.util.Optional;

public interface SiteRepository {
    Site save(Site site);

    Optional<Site> findById(Long siteId);

    void deleteById(Long siteId);

    int countByMemberId(Long memberId);

    List<Site> findAllByMemberId(Long memberId);
}
