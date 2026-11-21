package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.*;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.BusinessVerifyResult;
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

    // 사업자 인증 : IP 제한과 중복 확인을 거쳐 국세청 진위확인을 하고, 통과하면 가입 요청에 쓸 사업자 확인 토큰을 발급한다
    BusinessVerifyResult verifyBusiness(BusinessVerifyCommand command);

    // 자체 회원가입: 토큰을 확인하고 회원, 사업자 인증 기록, FREE 구독을 저장한 뒤 토큰을 소멸시킨다
    void signup(SignupCommand command);

}
