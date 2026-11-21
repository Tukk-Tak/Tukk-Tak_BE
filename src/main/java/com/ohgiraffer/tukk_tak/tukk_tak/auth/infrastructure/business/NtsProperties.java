package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.business;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/*
 * comment.
 *  국세청 API 설정 (application.yaml의 app.nts.*)
 *  serviceKey가 비어 있으면 BusinessVerifierConfig가 환경에 따라 가짜 검증기를 쓰거나 기동을 막는다.
 * */

@ConfigurationProperties(prefix = "app.nts")
public record NtsProperties(
        String serviceKey,                                                                      // 공공데이터포털 Decoding 키
        @DefaultValue("https://api.odcloud.kr/api/nts-businessman/v1") String baseUrl,          // API 기본 주소
        @DefaultValue("3s") Duration connectTimeout,                                            // 연결 제한 시간
        @DefaultValue("5s") Duration readTimeout
) {
}
