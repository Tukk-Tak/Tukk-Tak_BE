package com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.deployment.domain.model.DeploymentStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SiteQueryUsecase {

    SiteListResult getSiteList(Long memberId);

    record SiteListResult(
            int savedCount,
            int savedLimit,
            int publishedCount,
            int publishedLimit,
            List<SiteInfo> sites
    ) {}

    record SiteInfo(
            Long siteId,
            String title,
            SiteStatus siteStatus,
            Map<String, Object> theme,
            List<Map<String, Object>> rows,
            LocalDateTime lastSavedAt,
            DeploymentStatus deploymentStatus,   // DRAFT는 null
            String url,                          // DRAFT는 null
            LocalDateTime deployedAt             // DRAFT는 null
    ) {}
}
