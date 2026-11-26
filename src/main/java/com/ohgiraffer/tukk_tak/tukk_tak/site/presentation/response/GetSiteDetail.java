package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record GetSiteDetail(
        Long siteId,
        String title,
        SiteStatus siteStatus,
        Map<String, Object> theme,
        List<Map<String, Object>> rows,
        LocalDateTime lastSavedAt
) {
    public static GetSiteDetail from(SiteQueryUsecase.SiteDetailResult result) {
        return new GetSiteDetail(
                result.siteId(),
                result.title(),
                result.siteStatus(),
                result.theme(),
                result.rows(),
                result.lastSavedAt()
        );
    }
}
