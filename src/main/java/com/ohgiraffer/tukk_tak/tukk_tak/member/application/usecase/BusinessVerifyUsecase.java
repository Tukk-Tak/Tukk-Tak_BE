package com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.BusinessVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessVerifyResult;

/*
 * comment.
 *  사업자 인증 유스케이스
 * */

public interface BusinessVerifyUsecase {

    // 사업자 인증 : IP 제한과 중복 확인을 거쳐 국세청 진위확인을 하고, 통과하면 가입 요청에 쓸 사업자 확인 토큰을 발급한다
    BusinessVerifyResult verifyBusiness(BusinessVerifyCommand command);

}
