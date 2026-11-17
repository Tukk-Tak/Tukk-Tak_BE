package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "site_content")
public class SiteContentDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long siteId;

    private List<Map<String, Object>> blocks = new ArrayList<>();

    public static SiteContentDocument of(Long siteId) {
        SiteContentDocument d = new SiteContentDocument();
        d.siteId = siteId;
        return d;
    }
}