package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailSendCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.NicknameCheckCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.NicknameCheckResult;

/*
 * comment.
 *  회원가입 기능 목록 (API가 추가될 때마다 여기에 메서드를 늘린다)
 * */

public interface SignupCommandUsecase {

    // 이메일 인증번호 발송: 가입 여부와 발송 제한을 확인하고 6자리 인증번호를 메일로 보낸다
    EmailSendResult sendEmailCode(EmailSendCommand command);

    // 이메일 인증번호 검증: 번호가 맞으면 가입에 쓸 이메일 인증 토큰을 발급한다
    EmailVerifyResult verifyEmailCode(EmailVerifyCommand command);

    // 닉네임 중복 확인: 규칙을 검사하고 사용 가능한 닉네임인지 알려준다
    NicknameCheckResult checkNickname(NicknameCheckCommand command);

}
