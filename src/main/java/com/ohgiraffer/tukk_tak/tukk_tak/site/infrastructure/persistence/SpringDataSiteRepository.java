package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSiteRepository extends JpaRepository<SiteJpaEntity,Long> {

    int countByMemberId(Long memberId);
}
