package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberUpdateCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  내 정보 수정 요청
 *  보내지 않은(null) 항목은 바꾸지 않는다. 형식이 틀리면 컨트롤러에 들어오기 전에 400(COMMON-VALIDATION-ERROR)으로 응답된다.
 *  전화번호는 하이픈이 있어도 없어도 받고, 서버가 숫자만 남겨서 저장한다.
 *  이름, 이메일, 개인정보 이용 동의는 여기서 바꿀 수 없다.
 * */

public record MemberUpdateRequest (
        // 가입, 닉네임 확인 API와 같은 규칙 (한글, 영문, 숫자 2~15자)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,15}$", message = "닉네임은 한글, 영문, 숫자 2~15자로 입력해주세요.")
        String nickname,

        // 01로 시작하는 10~11자리 (필수 항목이라 빈 문자열로 지울 수 없다)
        @Pattern(regexp = "^01\\d-?\\d{3,4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone,

        // 빈 문자열이면 삭제
        @Pattern(regexp = "^(|\\d{2,4}-?\\d{3,4}-?\\d{4})$", message = "업장 전화번호 형식이 올바르지 않습니다.")
        String businessPhone,

        // 빈 문자열이면 삭제
        @Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
        String address,

        Boolean aiUsageAgreed
) {
    // 요청 DTO -> 명령 객체 (회원 id는 요청 본문이 아니라 토큰에서 꺼낸 값을 쓴다)
    public MemberUpdateCommand toCommand(Long memberId) {
        return new MemberUpdateCommand(memberId, nickname, phone, businessPhone, address, aiUsageAgreed);
    }
}
