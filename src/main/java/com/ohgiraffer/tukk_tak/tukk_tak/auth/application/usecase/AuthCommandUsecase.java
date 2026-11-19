package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.LoginCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.TokenResult;

/*
 * comment.
 *  인증 기능 목록 (로그인, 로그아웃, access token 재발급)
 * */

public interface AuthCommandUsecase {

    // 로그인: 이메일/비밀번호를 검증하고 토큰을 발급한다
    LoginResult login(LoginCommand command);

    // 로그아웃: access token은 블랙리스트에 올리고, refresh token은 삭제한다
    void logout(Long memberId, String bearerToken, String refreshToken);

    // 재발급: refresh token이 유효하면 새 access token을 돌려준다
    TokenResult reissueAccessToken(String refreshToken);

}
