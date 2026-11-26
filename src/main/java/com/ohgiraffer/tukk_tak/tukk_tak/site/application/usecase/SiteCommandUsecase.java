package com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.command.SaveSiteCommand;

import java.time.LocalDateTime;

public interface SiteCommandUsecase {
    SaveSiteResult saveSite(SaveSiteCommand saveSiteCommand);

    record SaveSiteResult(
            Long siteId,
            LocalDateTime lastSavedAt
    ) {}
}
