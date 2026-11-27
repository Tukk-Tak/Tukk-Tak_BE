package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteCommandUsecase;

public record UploadSiteImage(
        String url
) {
    public static UploadSiteImage from(SiteCommandUsecase.UploadImageResult result) {
        return new UploadSiteImage(result.url());
    }
}
