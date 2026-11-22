package com.ohgiraffer.tukk_tak.tukk_tak.member.application.command;

import java.time.LocalDate;

/*
 * comment.
 *  사업자 인증 명령
 *  컨트롤러(요청 DTO)에서 서비스로 넘기는 값 묶음이다. HTTP 용어(요청, 헤더)를 서비스가 몰라도 되게 한다.
 * */

public record BusinessVerifyCommand(
        String businessNumber,   // 사용자가 입력한 사업자등록번호 (하이픈 포함 가능, 정규화는 서비스에서)
        LocalDate openDate,      // 개업일자
        String ceoName,          // 대표자명 (가입 화면의 이름과 동기화된 값, 앞뒤 공백은 DTO에서 제거됨)
        String clientIp          // IP 요청 제한용
) {
}
