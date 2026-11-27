package com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.command.SaveSiteCommand;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface SiteCommandUsecase {
    SaveSiteResult saveSite(SaveSiteCommand saveSiteCommand);

    void deleteSite(Long siteId, Long memberId);

    UploadImageResult uploadImage(Long memberId, MultipartFile image);

    record UploadImageResult(String url) {}

    record SaveSiteResult(
            Long siteId,
            LocalDateTime lastSavedAt
    ) {}
}
