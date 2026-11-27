package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.request;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.command.SaveSiteCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record SaveSiteRequest(

        Long siteId,

        @NotBlank
        @Size(max = 100, message = "상호명은 100자 이하로 입력해 주세요.")
        String title,

        @NotNull
        Map<String, Object> theme,

        @NotNull
        List<Map<String, Object>> rows
) {
}
