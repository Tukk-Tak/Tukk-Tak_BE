package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "site_content")
public class SiteContentDocument {

    /**
     * 얘가 JpaEntity랑 같은 역할
     * 몽고 DB의 JpaEntity
     * */

    @Id
    private String id;

    @Indexed(unique = true)
    private Long siteId;

    private Map<String, Object> theme;

    private List<Map<String, Object>> rows;

    @Version
    private Long version;

    public static SiteContentDocument of(Long siteId, Map<String, Object> theme,
                                         List<Map<String, Object>> rows) {
        SiteContentDocument document = new SiteContentDocument();
        document.siteId = siteId;
        document.theme = theme;
        document.rows = rows;
        return document;
    }

    public void update(Map<String, Object> theme, List<Map<String, Object>> rows) {
        this.theme = theme;
        this.rows = rows;
    }
}