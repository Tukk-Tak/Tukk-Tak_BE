package com.ohgiraffer.tukk_tak.tukk_tak.member.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessCheckResult;

import java.time.LocalDate;

/*
 * comment.
 *  사업자 진위확인 포트
 *  서비스는 이 인터페이스만 알고, 국세청 API든 가짜든 구현체가 알아서 처리한다.
 *  이메일 발송(EmailSender)과 같은 방식이라, 국세청이 바뀌거나 막혀도 구현체만 바꾸면 된다.
 * */

public interface BusinessVerifier {

    // 사업자 정보가 국세청 정보와 일치하는지 확인한다.
    BusinessCheckResult verify(String businessNumber, LocalDate openDate, String ceoName);

}
