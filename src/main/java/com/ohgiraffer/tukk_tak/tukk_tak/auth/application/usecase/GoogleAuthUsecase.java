package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.GoogleLoginCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.GoogleSignupCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.GoogleLoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;

/*
 * comment.
 *  구글 인증 유스케이스 (컨트롤러가 의존하는 인터페이스)
 * */

public interface GoogleAuthUsecase {

    // 구글 ID 토큰으로 로그인하거나, 처음 온 계정이면 가입 티켓을 발급한다
    GoogleLoginResult googleLogin(GoogleLoginCommand command);

    // 구글 가입 완료: 티켓과 추가 정보로 회원을 만들고, 바로 로그인 토큰을 발급한다
    LoginResult googleSignup(GoogleSignupCommand command);

}
