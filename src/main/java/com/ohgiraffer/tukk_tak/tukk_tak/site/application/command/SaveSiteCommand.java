package com.ohgiraffer.tukk_tak.tukk_tak.site.application.command;

import java.util.List;
import java.util.Map;

public record SaveSiteCommand(
        Long memberId,
        Long siteId,
        String title,
        Map<String, Object> theme,
        List<Map<String, Object>> rows

) {
}
