package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SiteRepositoryAdapter implements SiteRepository {

    private final SpringDataSiteRepository springDataSiteRepository;

    @Override
    public Site save(Site site) {
        return springDataSiteRepository.save(SiteJpaEntity.fromDomain(site)).toDomain();
    }

    @Override
    public Optional<Site> findById(Long siteId) {
        return springDataSiteRepository.findById(siteId).map(SiteJpaEntity::toDomain);
    }
}
