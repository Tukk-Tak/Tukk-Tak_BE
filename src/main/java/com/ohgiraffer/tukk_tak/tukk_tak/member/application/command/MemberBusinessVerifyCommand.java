package com.ohgiraffer.tukk_tak.tukk_tak.member.application.command;

import java.time.LocalDate;

/*
 * comment.
 *  마이페이지 사업자 인증 명령
 *  대표자명은 받지 않고, 서비스가 회원 이름을 쓴다.
 * */

public record MemberBusinessVerifyCommand(
        Long memberId,           // 토큰에서 꺼낸 로그인 회원
        String businessNumber,   // 사용자가 입력한 사업자등록번호 (하이픈 포함 가능, 정규화는 서비스에서)
        LocalDate openDate       // 개업일자
) {
}
