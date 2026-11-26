package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteCommandUsecase;

import java.time.LocalDateTime;

public record SaveSiteResponse(
        Long siteId,
        LocalDateTime lastSavedAt
) {

    public static SaveSiteResponse from(SiteCommandUsecase.SaveSiteResult result) {
        return new SaveSiteResponse(result.siteId(), result.lastSavedAt());
    }
}
