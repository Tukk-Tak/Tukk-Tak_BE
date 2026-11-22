package com.ohgiraffer.tukk_tak.tukk_tak.member.application.result;

/*
 * comment.
 *  사업자 인증 결과
 *  서비스가 컨트롤러에 돌려주는 값이다.
 * */

public record BusinessVerifyResult(
        String businessToken    // 가입 요청에 넣을 사업자 확인 토큰 (유효 30분)
) {
}
