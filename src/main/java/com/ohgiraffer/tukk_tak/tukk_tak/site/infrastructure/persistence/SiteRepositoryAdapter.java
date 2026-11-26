package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public void deleteById(Long siteId) {
        springDataSiteRepository.deleteById(siteId);
        springDataSiteRepository.flush();
        // JPA의 deleteById는 DELETE를 바로 보내지 않고 커밋 때 실행하고
        // 몽고는 Jpa에 묶여있지 않아서 바로 삭제되니까
        // 몽고 삭제보다 먼저 DELETE가 실행되게 즉시 반영
    }

    @Override
    public int countByMemberId(Long memberId) {
        return springDataSiteRepository.countByMemberId(memberId);
    }

    @Override
    public List<Site> findAllByMemberId(Long memberId) {
        return springDataSiteRepository.findAllByMemberIdOrderByLastSavedAtDesc(memberId).stream()
                .map(SiteJpaEntity::toDomain)
                .toList();
    }
}
