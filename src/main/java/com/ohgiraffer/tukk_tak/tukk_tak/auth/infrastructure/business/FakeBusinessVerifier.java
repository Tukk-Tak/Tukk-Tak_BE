package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.business;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.BusinessCheckResult;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/*
 * comment.
 *  로컬 개발용 가짜 검증기
 *  국세청 키가 활성화되기 전에도 개발이 멈추지 않게 하려는 용도이다.
 *  기본은 항상 일치로 응답하고, 오류 흐름을 눈으로 확인할 수 있게 대표자명으로 결과를 바꿀 수 있다.
 *    대표자명 "불일치" -> 불일치 / 대표자명 "폐업" -> 폐업 사업자
 *  운영에서는 절대 쓰이면 안 되므로 BusinessVerifierConfig가 local 프로필에서만 만든다.
 * */

@Slf4j
public class FakeBusinessVerifier implements BusinessVerifier {

    @Override
    public BusinessCheckResult verify(String businessNumber, LocalDate openDate, String ceoName) {
        // 가짜가 동작 중이라는 것을 로그로 남겨서, 실수로 켜져 있어도 눈에 띄게 한다
        log.warn("[nts] 가짜 검증기 사용 중 (local 전용)");

        // 테스트용 분기: 대표자명으로 오류 흐름을 재현한다
        if ("불일치".equals(ceoName)) {
            return BusinessCheckResult.mismatch();
        }
        return BusinessCheckResult.matched("폐업".equals(ceoName));
    }

}
