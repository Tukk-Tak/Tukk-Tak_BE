package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.WithdrawalUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.WithdrawalRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/*
 * comment.
 *  회원 탈퇴 API 컨트롤러 (/members/me/withdrawal)
 *  주소는 마이페이지(/members/me)에 맞췄지만, 본인 확인과 로그인 끊기가 핵심이라 코드는 auth 도메인에 둔다.
 *  로그인한 회원만 호출할 수 있고(SecurityConfig의 anyRequest().authenticated()), 대상 회원은 토큰에서 꺼낸다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/withdrawal")
public class WithdrawalController {

    // refresh token을 담는 쿠키 이름 (AuthController와 같은 값)
    private static final String REFRESH_COOKIE = "refreshToken";

    private final WithdrawalUsecase withdrawalUsecase;

    // 로컬(http) 테스트 때만 false, 배포(https)에서는 true (AuthController와 같은 설정)
    @Value("${app.cookie.secure:true}") private boolean cookieSecure;
    // 쿠키를 공유할 도메인 (비어 있으면 지정하지 않음)
    @Value("${app.cookie.domain:}") private String cookieDomain;

    /*
     * comment.
     *  회원 탈퇴
     *  성공하면 모든 기기의 로그인이 끊기므로 refresh 쿠키를 지우고, 프론트는 로그아웃 상태의 첫 화면으로 이동한다.
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestBody @Valid WithdrawalRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        withdrawalUsecase.withdraw(request.toCommand(principal.getId()));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
                .body(ApiResponse.success(AuthResponseCode.WITHDRAWAL_COMPLETED, AuthResponseMessage.WITHDRAWAL_COMPLETED));
    }

    // 로그아웃 때와 같은 속성의 refresh 쿠키를 maxAge 0으로 만들어 브라우저가 지우게 한다
    // (속성이 하나라도 다르면 브라우저가 다른 쿠키로 보고 지우지 않는다)
    private ResponseCookie expiredRefreshCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax")
                .path("/auth")
                .maxAge(Duration.ZERO);
        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }

}
