package com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberBusinessVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;

/*
 * comment.
 *  마이페이지 사업자 인증 기능 목록
 * */

public interface MemberBusinessUsecase {

    // 사업자 인증(최초 인증과 수정 겸용): 국세청 진위확인을 통과하면 현재 사업자로 바꾼다
    MemberInfoResult.BusinessInfo verifyMyBusiness(MemberBusinessVerifyCommand command);

}
