package com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberUpdateCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;

/*
 * comment.
 *  마이페이지 회원 정보 유스케이스 (조회, 수정)
 * */

public interface MemberProfileUsecase {

    // 내 정보 조회 (현재 인증된 사업자 포함)
    MemberInfoResult getMyInfo(Long memberId);

    // 내 정보 수정 (닉네임, 전화번호, 업장 전화번호, 주소, AI 사용 동의), 수정된 정보를 돌려준다
    MemberInfoResult updateMyInfo(MemberUpdateCommand command);

}
