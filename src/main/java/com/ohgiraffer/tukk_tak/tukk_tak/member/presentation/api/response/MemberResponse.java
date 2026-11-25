package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  내 정보 응답 (조회와 수정이 같은 형태)
 *  business가 null이면 사업자 인증을 하지 않은 상태이다.
 * */

public record MemberResponse (
        String email,
        LoginType loginType,              // GOOGLE이면 프론트가 비밀번호 변경 메뉴를 숨긴다
        String name,
        String nickname,
        String phone,                     // 숫자만 (하이픈은 프론트가 붙인다)
        String businessPhone,
        String address,
        boolean privacyAgreed,
        boolean aiUsageAgreed,
        LocalDateTime aiUsageAgreedAt,    // AI 사용 동의 시각 (동의하지 않았으면 null)
        BusinessResponse business
) {
    public static MemberResponse from(MemberInfoResult result) {
        MemberInfoResult.BusinessInfo b = result.business();
        BusinessResponse business = b == null ? null
                : new BusinessResponse(b.businessNumber(), b.openDate(), b.ceoName(), b.verifiedAt());

        return new MemberResponse(result.email(), result.loginType(), result.name(), result.nickname(),
                result.phone(), result.businessPhone(), result.address(), result.privacyAgreed(),
                 result.aiUsageAgreed(), result.aiUsageAgreedAt(), business);
    }

    // 현재 인증된 사업자
    public record BusinessResponse(
            String businessNumber,        // 숫자 10자리 (하이픈과 마스킹은 프론트가 처리한다)
            LocalDate openDate,
            String ceoName,
            LocalDateTime verifiedAt
    ) {
        // 서비스 결과를 응답 형식으로 바꾼다
        public static BusinessResponse from(MemberInfoResult.BusinessInfo b) {
            return new BusinessResponse(b.businessNumber(), b.openDate(), b.ceoName(), b.verifiedAt());
        }
    }
}
