package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

/*
 * comment.
 *  국세청 조회 결과
 *  국세청 응답 형식(valid 01/02, b_stt_cd 01/02/03)을 서비스가 몰라도 되도록 필요한 두 값만 담는다.
 *  matched : 사업자번호, 개업일자, 대표자명이 국세청 정보와 모두 일치하는지
 *  closed  : 일치한 경우에 한해 폐업 상태인지 (휴업은 false)
 * */

public record BusinessCheckResult(
        boolean matched,
        boolean closed
) {
    // 불일치: 상태는 의미가 없으므로 closed는 항상 false
    public static BusinessCheckResult mismatch() {
        return new BusinessCheckResult(false, false);
    }

    // 일치: 폐업 여부만 같이 받는다
    public static BusinessCheckResult matched(boolean closed) {
        return new BusinessCheckResult(true, closed);
    }
}
