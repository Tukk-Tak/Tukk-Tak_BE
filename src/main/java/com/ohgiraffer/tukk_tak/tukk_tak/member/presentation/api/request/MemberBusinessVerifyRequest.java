package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberBusinessVerifyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/*
 * comment.
 *  마이페이지 사업자 인증 요청
 *  사업자번호는 하이픈이 있어도 없어도 받는다. (서버가 숫자 10자리로 정규화)
 *  대표자명은 받지 않는다. (회원 이름과 같아야 하므로 서버가 회원 이름을 쓴다)
 * */

public record MemberBusinessVerifyRequest(
        // 숫자 10자리 또는 000-00-00000 형식만 허용
        @NotBlank(message = "사업자등록번호를 입력해주세요.")
        @Pattern(regexp = "^(\\d{10}|\\d{3}-\\d{2}-\\d{5})$", message = "사업자등록번호 형식이 올바르지 않습니다.")
        String businessNumber,

        // yyyy-MM-dd 형식이고 오늘 이후(미래)는 허용하지 않는다
        @NotNull(message = "개업일자를 입력해주세요.")
        @PastOrPresent(message = "개업일자는 오늘 이전이어야 합니다.")
        LocalDate openDate
) {
    // 토큰에서 꺼낸 회원 ID를 붙여 command로 바꾼다
    public MemberBusinessVerifyCommand toCommand(Long memberId) {
        return new MemberBusinessVerifyCommand(memberId, businessNumber, openDate);
    }
}
