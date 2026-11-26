package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.scheduler;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.event.SiteImageCleanupEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteContentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence.SiteJpaEntity;
import com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence.SpringDataSiteContentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence.SpringDataSiteRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.support.SiteImageKeyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SiteDraftDeleteScheduler {

    private static final int RETENTION_DAYS = 180;
    private static final int BATCH_SIZE = 500;

    private final SpringDataSiteRepository springDataSiteRepository;
    private final SpringDataSiteContentRepository springDataSiteContentRepository;
    private final SiteContentRepository siteContentRepository;
    private final SiteImageKeyExtractor siteImageKeyExtractor;
    private final ApplicationEventPublisher eventPublisher;

    // 매일 새벽 4시에 180일이 지난 DRAFT 사이트를 한 번에 최대 500개씩 삭제
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpiredDraftSites() {
        List<SiteJpaEntity> sites = springDataSiteRepository.findByStatusAndCreatedAtBeforeOrderById(
                SiteStatus.DRAFT,
                LocalDateTime.now().minusDays(RETENTION_DAYS),
                PageRequest.of(0, BATCH_SIZE));

        if (sites.isEmpty()) {
            return;
        }

        List<Long> siteIds = sites.stream().map(SiteJpaEntity::getId).toList();

        // 몽고를 지우면 rows를 읽을 수 없어서 삭제 전에 지울 사진 key를 미리 모아둠
        Map<Long, SiteContentRepository.SiteContent> contents = siteContentRepository.findAllBySiteIdIn(siteIds);
        Set<String> imageKeys = new HashSet<>();
        for (SiteJpaEntity site : sites) {
            SiteContentRepository.SiteContent content = contents.get(site.getId());
            if (content != null) {
                imageKeys.addAll(siteImageKeyExtractor.extract(site.getMemberId(), content.rows()));
            }
        }

        // 몽고에서 예외가 나면 mysql 삭제도 롤백
        springDataSiteRepository.deleteAllByIdIn(siteIds);
        springDataSiteContentRepository.deleteBySiteIdIn(siteIds);

        // 커밋 이후에 S3 사진 삭제
        if (!imageKeys.isEmpty()) {
            eventPublisher.publishEvent(new SiteImageCleanupEvent(imageKeys));
        }

        log.info("만료 미배포 사이트 삭제 완료: {}건, 사진 {}장 정리 예정", siteIds.size(), imageKeys.size());
    }
}