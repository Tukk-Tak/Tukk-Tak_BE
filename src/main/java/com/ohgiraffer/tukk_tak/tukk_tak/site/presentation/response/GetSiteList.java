package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.deployment.domain.model.DeploymentStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record GetSiteList(
        int savedCount,
        int savedLimit,
        int publishedCount,
        int publishedLimit,
        List<SiteInfo> sites
) {
    public record SiteInfo(
            Long siteId,
            String title,
            SiteStatus siteStatus,
            Map<String, Object> theme,
            List<Map<String, Object>> rows,
            LocalDateTime lastSavedAt,
            DeploymentStatus deploymentStatus,
            String url,
            LocalDateTime deployedAt
            ) {}

    public static GetSiteList from(SiteQueryUsecase.SiteListResult result) {
        List<SiteInfo> sites = result.sites().stream()
                .map(site -> new SiteInfo(
                        site.siteId(),
                        site.title(),
                        site.siteStatus(),
                        site.theme(),
                        site.rows(),
                        site.lastSavedAt(),
                        site.deploymentStatus(),
                        site.url(),
                        site.deployedAt()
                ))
                .toList();

        return new GetSiteList(
                result.savedCount(),
                result.savedLimit(),
                result.publishedCount(),
                result.publishedLimit(),
                sites
        );
    }
}