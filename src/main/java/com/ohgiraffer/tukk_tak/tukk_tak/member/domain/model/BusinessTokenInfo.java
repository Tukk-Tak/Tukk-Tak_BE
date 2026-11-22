package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model;

import java.time.LocalDate;

/*
 * comment.
 *  사업자 확인 토큰에 묶여 Redis에 저장되는 값
 *  5번 가입 API가 이 값으로 "가입 이름 = 대표자명"인지 다시 비교하고,
 *  가입 시 business_verification 테이블에 저장할 값도 여기서 꺼낸다.
 * */

public record BusinessTokenInfo(
        String businessNumber,   // 숫자 10자리로 정규화된 사업자번호
        LocalDate openDate,      // 개업일자
        String ceoName           // 국세청에서 일치를 확인한 대표자명
) {
}
