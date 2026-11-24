package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.jwt.JwtTokenProvider;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.RefreshTokenService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.PrincipalCache;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/*
 * comment.
 *  로그인 성공 뒤 토큰을 발급하는 공통 처리
 *  이메일 로그인과 구글 로그인이 같은 발급 규칙(access + refresh, Redis 저장)을 쓰도록 한곳에 모았다.
 *  비밀번호나 구글 토큰 검증은 끝난 뒤에 호출해야 한다.
 * */

@Component
@RequiredArgsConstructor
public class LoginTokenIssuer {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PrincipalCache principalCache;

    public LoginResult issue(Member member, boolean rememberMe) {

        // 1. 인증 필터가 매 요청마다 DB를 읽지 않도록 회원 정보를 Redis에 캐시
        principalCache.put(member);

        // 2. refresh token 유효기간 결정: 로그인 유지면 14일, 아니면 1일
        Duration refreshTtl = rememberMe
                ? jwtTokenProvider.getRefreshTokenValidity()
                : jwtTokenProvider.getSessionRefreshTokenValidity();

        // 3. 토큰 발급 (refresh token의 만료시각도 위에서 정한 기간과 같게 만든다)
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), refreshTtl);

        // 4. refresh token을 Redis에 저장 (회원당 1개: 다른 기기에서 로그인하면 덮어써짐)
        refreshTokenService.save(member.getId(), refreshToken, refreshTtl);

        return new LoginResult(accessToken, refreshToken, refreshTtl,
                rememberMe, member.getRole(), member.getNickname());
    }

}
