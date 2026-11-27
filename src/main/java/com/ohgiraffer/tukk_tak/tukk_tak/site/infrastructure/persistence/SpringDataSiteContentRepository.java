package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataSiteContentRepository extends MongoRepository<SiteContentDocument, String> {

    Optional<SiteContentDocument> findBySiteId(Long siteId);

    @Query(value = "{ 'siteId': ?0 }", delete = true)
    void deleteBySiteId(Long siteId);

    List<SiteContentDocument> findAllBySiteIdIn(List<Long> siteIds);

    @Query(value = "{ 'siteId': { $in: ?0 } }", delete = true)
    void deleteBySiteIdIn(List<Long> siteIds);
}