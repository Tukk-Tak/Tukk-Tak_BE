package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.GoogleLoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.AuthCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.GoogleAuthUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.GoogleLoginRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.GoogleSignupRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.LoginRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.GoogleLoginResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.LoginResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.TokenResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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
    private final GoogleAuthUsecase googleAuthUsecase;

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

    /*
     * comment.
     *  구글 로그인 (구글 계정으로 계속하기)
     *  이미 가입한 구글 회원이면 로그인하고 refresh 쿠키를 내려주고,
     *  처음 온 계정이면 쿠키 없이 가입 티켓과 구글 정보를 내려준다. (둘 다 200, registered 값으로 구분)
     * */
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<GoogleLoginResponse>> googleLogin(
            @RequestBody @Valid GoogleLoginRequest request, HttpServletRequest httpRequest) {
        GoogleLoginResult result = googleAuthUsecase.googleLogin(
                request.toCommand(ClientIpResolver.resolve(httpRequest)));

        // 처음 온 계정: 아직 로그인한 것이 아니므로 쿠키 없이 가입 티켓만 내려준다
        if (!result.registered()) {
            return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.GOOGLE_SIGNUP_REQUIRED,
                    AuthResponseMessage.GOOGLE_SIGNUP_REQUIRED, GoogleLoginResponse.from(result)));
        }

        // 이미 가입한 회원: 로그인과 같은 방식으로 refresh 쿠키를 내려준다
        LoginResult login = result.login();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(login.refreshToken(), cookieMaxAge(login)).toString())
                .body(ApiResponse.success(AuthResponseCode.GOOGLE_LOGIN_SUCCESS,
                        AuthResponseMessage.GOOGLE_LOGIN_SUCCESS, GoogleLoginResponse.from(result)));
    }

    /*
     * comment.
     *  구글 회원가입 완료 (구글 로그인에서 받은 가입 티켓과 추가 정보로 가입)
     *  비밀번호가 없어서 로그인 화면으로 보낼 수 없으므로, 가입 직후 바로 로그인 처리해서 토큰과 쿠키를 내려준다.
     * */
    @PostMapping("/google/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> googleSignup(
            @RequestBody @Valid GoogleSignupRequest request) {
        LoginResult result = googleAuthUsecase.googleSignup(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken(), cookieMaxAge(result)).toString())
                .body(ApiResponse.success(AuthResponseCode.GOOGLE_SIGNUP_SUCCESS,
                        AuthResponseMessage.GOOGLE_SIGNUP_SUCCESS, LoginResponse.from(result)));
    }

    // refresh 쿠키 생성: maxAge가 null이면 세션 쿠키가 된다
    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)       // 자바스크립트에서 못 읽게 해서 XSS로 탈취되는 것을 막음
                .secure(cookieSecure) // https에서만 전송
                .sameSite(cookieSecure ? "None" : "Lax")    // 프론트와 도메인이 다른 환경에서도 쿠키 전송 (secure 필수)
                .path("/auth");       // /auth 요청에만 쿠키가 따라가도록 범위 제한
        if (maxAge != null) {
            builder.maxAge(maxAge);
        }
        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }

    // 유지 체크면 쿠키 수명을 지정하고, 아니면 null(세션 쿠키)
    private Duration cookieMaxAge(LoginResult result) {
        return result.rememberMe() ? result.refreshTtl() : null;
    }

}
