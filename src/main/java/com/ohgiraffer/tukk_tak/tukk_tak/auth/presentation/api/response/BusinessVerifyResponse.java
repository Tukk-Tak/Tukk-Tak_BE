package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.BusinessVerifyResult;

/*
 * comment.
 *  사업자 인증 응답
 *  프론트는 businessToken을 보관했다가 회원가입 요청(/auth/signup)에 그대로 넣어 보낸다.
 *  이름을 바꾸면 대표자명과 달라지므로, 프론트는 이 토큰을 버리고 "인증 완료"를 해제해야 한다.
 * */

public record BusinessVerifyResponse(
        String businessToken
) {
    // 서비스 결과를 응답 형식으로 바꾼다
    public static BusinessVerifyResponse from(BusinessVerifyResult result) {
        return new BusinessVerifyResponse(result.businessToken());
    }
}
