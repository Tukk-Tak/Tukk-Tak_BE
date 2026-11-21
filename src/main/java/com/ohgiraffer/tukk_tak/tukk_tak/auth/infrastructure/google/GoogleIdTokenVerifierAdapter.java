package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.google;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;

import java.util.List;

/*
 * comment.
 *  구글 ID 토큰 실제 검증기
 *  구글 공개키(JWKS)로 서명을 확인하고, 만료, 발급자, 발급 대상(우리 클라이언트 ID), 이메일 인증 여부를 검사한다.
 *  공개키는 라이브러리가 캐시하므로 요청마다 구글에 접속하지 않는다.
 * */

public class GoogleIdTokenVerifierAdapter implements GoogleTokenVerifier {

    // 구글이 ID 토큰을 서명하는 공개키 목록 주소
    private static final String JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    // 구글이 쓰는 발급자 값 (두 가지 형태가 모두 쓰인다)
    private static final List<String> ISSUERS = List.of("https://accounts.google.com", "accounts.google.com");

    private final NimbusJwtDecoder jwtDecoder;

    public GoogleIdTokenVerifierAdapter(String clientId) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).build();

        // 발급자와 발급 대상 검사 (다른 서비스용으로 발급된 구글 토큰을 걸러낸다)
        OAuth2TokenValidator<Jwt> issuerAndAudience = jwt -> {
            boolean issuerOk = ISSUERS.contains(String.valueOf(jwt.getClaims().get("iss")));
            boolean audienceOk = jwt.getAudience() != null && jwt.getAudience().contains(clientId);
            if (issuerOk && audienceOk) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "발급자 또는 발급 대상이 다릅니다.", null));
        };

        // 기본 검사(만료, 시작 시각)와 위 검사를 함께 적용
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), issuerAndAudience));
        this.jwtDecoder = decoder;
    }

    @Override
    public GoogleIdentity verify(String credential) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(credential);
        } catch (JwtException e) {
            throw AuthException.googleTokenInvalid();
        }

        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw AuthException.googleTokenInvalid();
        }
        if (!Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            throw AuthException.googleEmailUnverified();
        }

        String name = jwt.getClaimAsString("name");
        return new GoogleIdentity(email, name == null ? "" : name);
    }

}
