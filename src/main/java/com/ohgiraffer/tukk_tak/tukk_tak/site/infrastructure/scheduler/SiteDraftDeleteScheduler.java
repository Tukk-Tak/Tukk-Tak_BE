package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.scheduler;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence.SpringDataSiteContentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence.SpringDataSiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SiteDraftDeleteScheduler {

    private static final int RETENTION_DAYS = 180;
    private static final int BATCH_SIZE = 500;
    // 하루에 최대 500개만 삭제

    private final SpringDataSiteRepository springDataSiteRepository;
    private final SpringDataSiteContentRepository springDataSiteContentRepository;

    // 매일 새벽 4시에 저장한 지 180일 지난 DRAFT 사이트 삭제
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpiredDraftSites() {
        List<Long> siteIds = springDataSiteRepository.findIdsByStatusAndCreatedAtBefore(
                SiteStatus.DRAFT,
                LocalDateTime.now().minusDays(RETENTION_DAYS),
                PageRequest.of(0, BATCH_SIZE));

        if (siteIds.isEmpty()) {
            return;
        }

        // 몽고에서 예외가 나면 MySQL 삭제도 롤백됨
        springDataSiteRepository.deleteAllByIdIn(siteIds);
        springDataSiteContentRepository.deleteBySiteIdIn(siteIds);

        log.info("만료 미배포 사이트 삭제 완료: {}건", siteIds.size());
    }
}