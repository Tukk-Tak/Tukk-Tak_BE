package com.ohgiraffer.tukk_tak.tukk_tak.member.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  내 정보 조회 결과
 *  business는 가장 최근에 인증한 사업자이고, 인증한 적이 없으면 null이다.
 * */

public record MemberInfoResult(
        String email,
        LoginType loginType,              // 화면에서 비밀번호 변경 메뉴를 보일지 정하는 데 쓴다 (GOOGLE이면 숨김)
        String name,
        String nickname,
        String phone,                     // 숫자만 (하이픈은 프론트가 붙인다)
        String businessPhone,
        String address,
        boolean privacyAgreed,
        boolean aiUsageAgreed,
        LocalDateTime aiUsageAgreedAt,    // AI 사용 동의 시각 (동의하지 않았으면 null)
        BusinessInfo business
) {
    // 현재 인증된 사업자
    public record BusinessInfo(
            String businessNumber,        // 숫자 10자리
            LocalDate openDate,
            String ceoName,
            LocalDateTime verifiedAt
    ) {
    }
}
