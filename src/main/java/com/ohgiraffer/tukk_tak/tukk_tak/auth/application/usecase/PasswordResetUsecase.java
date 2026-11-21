package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailSendCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordResetCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.PasswordResetVerifyResult;

/*
 * comment.
 *  비밀번호 재설정 기능 목록 (로그인 전에 이메일 인증으로 본인을 확인하고 새 비밀번호를 정한다)
 * */

public interface PasswordResetUsecase {

    // 재설정 인증번호 발송: 재설정할 수 있는 회원인지와 발송 제한을 확인하고 6자리 인증번호를 메일로 보낸다
    EmailSendResult sendResetCode(EmailSendCommand command);

    // 재설정 인증번호 검증: 번호가 맞으면 새 비밀번호를 설정할 때 쓰는 재설정 토큰을 발급한다
    PasswordResetVerifyResult verifyResetCode(EmailVerifyCommand command);

    // 비밀번호 재설정: 토큰을 확인하고 비밀번호를 바꾼 뒤, 다른 기기의 로그인을 끊고 로그인 잠금을 푼다
    void resetPassword(PasswordResetCommand command);

}
