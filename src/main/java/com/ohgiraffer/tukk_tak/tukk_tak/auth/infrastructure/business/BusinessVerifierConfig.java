package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.business;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

/*
 * comment.
 *  사업자 검증기 선택
 *  1) 키가 있으면: 어떤 환경이든 국세청 구현체
 *  2) 키가 없고 local 프로필이면: 가짜 검증기 (경고 로그)
 *  3) 키가 없고 local이 아니면: 앱이 아예 뜨지 않게 막는다 (가짜가 운영에 섞이는 사고 방지)
 * */

@Slf4j
@Configuration
@EnableConfigurationProperties(NtsProperties.class)
public class BusinessVerifierConfig {

    @Bean
    public BusinessVerifier businessVerifier(NtsProperties properties, Environment environment) {
        // 1. 키가 있으면 실제 국세청 구현체
        if (StringUtils.hasText(properties.serviceKey())) {
            return new NtsBusinessVerifier(properties);
        }

        // 2. 키가 없어도 local 프로필이면 가짜로 개발을 이어간다
        if (environment.acceptsProfiles(Profiles.of("local"))) {
            log.warn("[nts] NTS_SERVICE_KEY가 비어 있어 가짜 검증기를 사용합니다. (local 전용)");
            return new FakeBusinessVerifier();
        }

        // 3. 그 외 환경에서 키가 없으면 기동을 막는다
        throw new IllegalStateException("NTS_SERVICE_KEY가 설정되지 않았습니다. 운영 환경에서는 필수입니다.");
    }

    /*
     * comment.
     *  사업자 검증기 생성
     *  국세청 인증키가 없으면 어떤 환경에서도 앱이 뜨지 않게 막는다.
     *  (키 없이 사업자 인증이 통과되는 일이 없도록)
     * */
    // @Bean
    //    public BusinessVerifier businessVerifier(NtsProperties properties) {
    //        // 키가 없으면 기동 자체를 막는다
    //        if (!StringUtils.hasText(properties.serviceKey())) {
    //            throw new IllegalStateException("NTS_SERVICE_KEY가 설정되지 않았습니다.");
    //        }
    //        return new NtsBusinessVerifier(properties);
    //    }

}
