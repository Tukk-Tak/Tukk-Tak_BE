package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.BusinessVerifyCommand;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/*
 * comment.
 *  사업자 인증 요청
 *  사업자번호는 하이픈이 있어도 없어도 받는다. (서버가 숫자 10자리로 정규화)
 *  개업일자는 yyyy-MM-dd 형식이고, 미래 날짜는 400이다.
 *  이상한 값은 컨트롤러에 들어오기 전에 400(COMMON-VALIDATION-ERROR)으로 걸러낸다.
 * */

public record BusinessVerifyRequest (
        // 숫자 10자리 또는 000-00-00000 형식만 허용
        @NotBlank(message = "사업자등록번호를 입력해주세요.")
        @Pattern(regexp = "^(\\d{10}|\\d{3}-\\d{2}-\\d{5})$", message = "사업자등록번호 형식이 올바르지 않습니다.")
        String businessNumber,

        // yyyy-MM-dd 형식이고 오늘 이후(미래)는 허용하지 않는다
        @NotNull(message = "개업일자를 입력해주세요.")
        @PastOrPresent(message = "개업일자는 오늘 이전이어야 합니다.")
        LocalDate openDate,

        // 가입 화면의 이름과 동기화된 값이다. DB 컬럼(ceo_name)이 50자이므로 같은 길이로 제한
        @NotBlank(message = "대표자명을 입력해주세요.")
        @Size(max = 50, message = "대표자명은 50자 이하로 입력해주세요.")
        String ceoName
) {
    // 컨트롤러에서 IP를 받아 command로 바꾼다. 대표자명은 앞뒤 공백을 제거해서 넘긴다
    public BusinessVerifyCommand toCommand(String clientIp) {
        return new BusinessVerifyCommand(businessNumber, openDate, ceoName.trim(), clientIp);
    }
}
