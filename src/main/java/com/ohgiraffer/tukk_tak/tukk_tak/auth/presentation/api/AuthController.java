package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.AuthCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.LoginRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.LoginResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.TokenResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/*
 * comment.
 *  인증 API 컨트롤러 (/auth/login, /auth/logout, /auth/refresh)
 *  refresh token은 HttpOnly 쿠키로 주고받는다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    // refresh token을 담는 쿠키 이름
    private static final String REFRESH_COOKIE = "refreshToken";

    private final AuthCommandUsecase authCommandUsecase;

    // 로컬(http) 테스트 때만 false, 배포(https)에서는 true
    @Value("${app.cookie.secure:true}") private boolean cookieSecure;
    // 쿠키를 공유할 도메인 (비어 있으면 지정하지 않음)
    @Value("${app.cookie.domain:}") private String cookieDomain;

    /*
     * comment.
     *  로그인
     *  "로그인 상태 유지"를 체크하면 refresh 쿠키를 유지 쿠키(maxAge 지정)로,
     *  체크하지 않으면 세션 쿠키(maxAge 없음, 브라우저를 닫으면 삭제)로 내려준다.
     * */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request,
                                                            HttpServletRequest httpRequest) {
        // 요청 DTO -> 명령 객체로 변환해서 서비스 호출
        LoginResult result = authCommandUsecase.login(
                request.toCommand(ClientIpResolver.resolve(httpRequest)));

        // 유지 체크: 쿠키 수명 지정 / 미체크: null이면 세션 쿠키
        Duration cookieMaxAge = result.rememberMe() ? result.refreshTtl() : null;

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken(), cookieMaxAge).toString())
                .body(ApiResponse.success(AuthResponseCode.LOGIN_SUCCESS,
                        AuthResponseMessage.LOGIN_SUCCESS, LoginResponse.from(result)));
    }

    /*
     * comment.
     *  로그아웃 (로그인한 사용자만 호출 가능)
     *  refresh 쿠키를 즉시 만료시켜서 브라우저에서도 지운다.
     * */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        authCommandUsecase.logout(principal.getId(), bearerToken, refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString())   // maxAge 0 = 삭제
                .body(ApiResponse.success(AuthResponseCode.LOGOUT_SUCCESS, AuthResponseMessage.LOGOUT_SUCCESS));
    }

    /*
     * comment.
     *  access token 재발급 (access token이 만료됐을 때 프론트가 호출)
     *  refresh 쿠키가 자동으로 함께 전송된다.
     * */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.TOKEN_REFRESHED,
                AuthResponseMessage.TOKEN_REFRESHED,
                TokenResponse.from(authCommandUsecase.reissueAccessToken(refreshToken))));
    }

    // refresh 쿠키 생성: maxAge가 null이면 세션 쿠키가 된다
    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)       // 자바스크립트에서 못 읽게 해서 XSS로 탈취되는 것을 막음
                .secure(cookieSecure) // https에서만 전송
                .sameSite("None")     // 프론트와 도메인이 다른 환경에서도 쿠키 전송 (secure 필수)
                .path("/auth");       // /auth 요청에만 쿠키가 따라가도록 범위 제한
        if (maxAge != null) {
            builder.maxAge(maxAge);
        }
        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }

}
