package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataSiteContentRepository extends MongoRepository<SiteContentDocument, String> {

    Optional<SiteContentDocument> findBySiteId(Long siteId);

}