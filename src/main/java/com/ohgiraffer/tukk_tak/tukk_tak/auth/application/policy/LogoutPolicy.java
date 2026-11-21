package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import org.springframework.stereotype.Component;

/*
 * comment.
 *  로그아웃 요청 형식 검증 (Authorization 헤더, refresh token 쿠키 존재 여부)
 * */

// 로그인은 읽기만 하므로 @Transactional을 걸지 않음
// BCrypt가 도는 동안 DB 커넥션을 쥐고 있지 않게 하려는 의도
@Component
public class LogoutPolicy {

    private static final String BEARER_PREFIX = "Bearer ";

    public static String resolveAccessToken(String bearerToken) {
        if (bearerToken == null) {
            throw AuthException.missingAccessToken();
        }
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw AuthException.invalidToken();
        }
        return bearerToken.substring(BEARER_PREFIX.length());
    }

    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw AuthException.missingRefreshToken();
        }
    }

}
