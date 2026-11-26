package com.ohgiraffer.tukk_tak.tukk_tak.site.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SiteImageKeyExtractor {

    /**
     * 사이트를 삭제할 때 S3에서 지울 사진을 찾기
     * 서버는 rows 구조를 모르기 대문에 rows 전체를 끝까지 훑으면서
     * 주소에 내 memberId가 있는 애를 찾아서 S3 key로 바꿔서 돌려줌
     * 이 클래스는 key 목록만 만들고 실제 S3 삭제X
     */

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    // rows 안에서 내 사진 URL만 골라 S3 key로 반환
    public Set<String> extract(Long memberId, List<?> rows) {
        String prefix = cloudFrontDomain + "/site/" + memberId + "/";
        Set<String> keys = new HashSet<>();
        collect(rows, prefix, keys);
        return keys;
    }

    private void collect(Object node, String prefix, Set<String> keys) {
        if (node instanceof Map<?, ?> map) {
            map.values().forEach(value -> collect(value, prefix, keys));
        } else if (node instanceof Collection<?> list) {
            list.forEach(value -> collect(value, prefix, keys));
        } else if (node instanceof String text && text.startsWith(prefix)) {
            keys.add(text.substring(cloudFrontDomain.length() + 1));
        }
    }
}
