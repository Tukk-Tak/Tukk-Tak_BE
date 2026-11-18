package com.ohgiraffer.tukk_tak.tukk_tak.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/*
 * comment.
 *  JWT 생성 및 검증 담당
 *  토큰마다 고유 id(jti)와 타입(ACCESS / REFRESH)을 넣어서,
 *  access 토큰을 refresh 자리에 쓰는 것을 막고 로그아웃 처리(블랙리스트)에 사용한다.
 * */

@Component
public class JwtTokenProvider {

    // 토큰 안에 "이 토큰이 어떤 종류인지" 적어두는 claim 이름과 값
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    // 서명에 쓰는 비밀키 문자열 (32바이트 이상, 환경변수 JWT_SECRET)
    @Value("${jwt.secret}") private String secretKey;
    // access token 유효기간 (ms)
    @Value("${jwt.access-token-validity}") private long accessTokenValidityMs;
    // "로그인 상태 유지"를 체크했을 때의 refresh token 유효기간 (ms)
    @Value("${jwt.refresh-token-validity}") private long refreshTokenValidityMs;
    // "로그인 상태 유지"를 체크하지 않았을 때의 refresh token 유효기간 (ms)
    @Value("${jwt.refresh-token-session-validity}") private long sessionRefreshTokenValidityMs;

    // 문자열 비밀키를 서명용 키 객체로 바꾼 것 (매번 만들지 않도록 한 번만 생성)
    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * comment.
     *  access token 생성 (API 호출용, 짧은 유효기간)
     * */
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        return createToken(memberId, now, new Date(now.getTime() + accessTokenValidityMs), ACCESS_TOKEN_TYPE);
    }

    /*
     * comment.
     *  refresh token 생성: 호출하는 쪽이 유효기간을 정한다 ("로그인 상태 유지" 여부에 따라 다름)
     *  JWT 만료시각이 Redis 저장 기간과 같아야 하므로 같은 값을 넘겨서 쓴다.
     * */
    public String createRefreshToken(Long memberId, Duration validity) {
        Date now = new Date();
        return createToken(memberId, now, new Date(now.getTime() + validity.toMillis()), REFRESH_TOKEN_TYPE);
    }

    // 실제 토큰을 만드는 공통 메서드
    private String createToken(Long memberId, Date issuedAt, Date expiration, String tokenType) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())        // jti: 토큰마다 다른 고유 id (로그아웃 블랙리스트에 사용)
                .subject(String.valueOf(memberId))       // 토큰 주인(회원 id)
                .claim(TOKEN_TYPE_KEY, tokenType)        // ACCESS 또는 REFRESH
                .issuedAt(issuedAt)                      // 발급 시각
                .expiration(expiration)                  // 만료 시각
                .signWith(key)                           // 비밀키로 서명 (위조 방지)
                .compact();
    }

    // access token 검증 후 내용(claims) 반환. 실패하면 null
    public Claims resolveAccessClaims(String token) { return resolveClaims(token, ACCESS_TOKEN_TYPE); }

    // refresh token 검증 후 내용(claims) 반환. 실패하면 null
    public Claims resolveRefreshClaims(String token) { return resolveClaims(token, REFRESH_TOKEN_TYPE); }

    /*
     * comment.
     *  토큰 검증 공통 처리
     *  서명, 만료를 검사하고, 토큰 종류(type)가 기대한 것과 같은지도 확인한다.
     *  예외 대신 null을 돌려주므로 호출하는 쪽에서 null 여부로 실패를 판단한다.
     * */
    private Claims resolveClaims(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            // access 자리에 refresh를 쓰거나 그 반대인 경우를 걸러낸다
            return expectedType.equals(claims.get(TOKEN_TYPE_KEY, String.class)) ? claims : null;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 만료, 형식 오류, 빈 문자열 등
            return null;
        }
    }

    // 토큰 주인(회원 id) 꺼내기
    public Long extractMemberId(Claims claims) { return Long.parseLong(claims.getSubject()); }

    // 토큰 고유 id(jti) 꺼내기
    public String extractJti(Claims claims) { return claims.getId(); }

    // 토큰의 남은 유효시간(ms). 로그아웃 블랙리스트의 저장 기간으로 사용
    public long getRemainingValidityMs(Claims claims) {
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 요청의 Authorization 헤더("Bearer xxx")에서 토큰 문자열만 꺼내기. 없으면 null
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ") && bearer.length() > 7) {
            return bearer.substring(7);
        }
        return null;
    }

    // "로그인 상태 유지" 체크 시 유효기간 (14일)
    public Duration getRefreshTokenValidity() { return Duration.ofMillis(refreshTokenValidityMs); }

    // "로그인 상태 유지" 미체크 시 유효기간 (1일)
    public Duration getSessionRefreshTokenValidity() { return Duration.ofMillis(sessionRefreshTokenValidityMs); }

}
