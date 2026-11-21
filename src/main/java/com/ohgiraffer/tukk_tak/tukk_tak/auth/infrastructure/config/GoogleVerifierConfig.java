package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.config;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.adapter.FakeGoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.adapter.GoogleIdTokenVerifierAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

/*
 * comment.
 *  구글 토큰 검증기 선택 설정
 *  클라이언트 ID가 있으면 실제 검증기, 없고 local 프로파일이면 가짜 검증기를 쓴다.
 *  그 외 환경에서 ID가 없으면 서버가 뜨지 않게 해서, 가짜 검증기가 운영에 올라가는 사고를 막는다.
 * */

@Slf4j
@Configuration
public class GoogleVerifierConfig {

    @Bean
    public GoogleTokenVerifier googleTokenVerifier(@Value("${app.google.client-id:}") String clientId,
                                                   Environment environment) {
        if (StringUtils.hasText(clientId)) {
            return new GoogleIdTokenVerifierAdapter(clientId);
        }
        if (environment.acceptsProfiles(Profiles.of("local"))) {
            log.warn("[google] 클라이언트 ID가 없어 가짜 검증기를 사용합니다. (local 전용)");
            return new FakeGoogleTokenVerifier();
        }
        throw new IllegalStateException("app.google.client-id 가 없습니다. local 이외 환경에서는 필수입니다.");
    }

}
