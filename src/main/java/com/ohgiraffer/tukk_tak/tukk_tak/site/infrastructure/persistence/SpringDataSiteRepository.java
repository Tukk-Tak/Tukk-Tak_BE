package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataSiteRepository extends JpaRepository<SiteJpaEntity,Long> {

    int countByMemberId(Long memberId);

    List<SiteJpaEntity> findAllByMemberIdOrderByLastSavedAtDesc(Long memberId);

    @Query("select s.id from SiteJpaEntity s where s.status = :status and s.createdAt < :threshold order by s.id")
    List<Long> findIdsByStatusAndCreatedAtBefore(
            @Param("status") SiteStatus status,
            @Param("threshold") LocalDateTime threshold,
            Pageable pageable);

    @Modifying
    @Query("delete from SiteJpaEntity s where s.id in :ids")
    int deleteAllByIdIn(@Param("ids") List<Long> ids);

    List<SiteJpaEntity> findByStatusAndCreatedAtBeforeOrderById(
            SiteStatus status, LocalDateTime threshold, Pageable pageable);
}
