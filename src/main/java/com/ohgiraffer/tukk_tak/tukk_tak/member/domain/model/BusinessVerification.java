package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  사업자 인증 기록 도메인 모델 (DB를 모르는 순수 자바 객체)
 *  인증에 성공(APPROVED)한 건만 저장한다. 불일치나 국세청 장애는 기록하지 않고 응답으로만 알린다.
 * */

@Getter
public class BusinessVerification {

    private final Long id;
    private final Long memberId;
    private final String businessNumber;        // 숫자 10자리로 정규화된 값
    private final LocalDate openDate;
    private final String ceoName;               // 국세청에서 일치를 확인한 대표자명
    private final VerifyMethod verifyMethod;
    private final BusinessVerifyStatus status;
    private final LocalDateTime verifiedAt;

    public BusinessVerification(Long id, Long memberId, String businessNumber, LocalDate openDate,
                                String ceoName, VerifyMethod verifyMethod,
                                BusinessVerifyStatus status, LocalDateTime verifiedAt) {
        this.id = id;
        this.memberId = memberId;
        this.businessNumber = businessNumber;
        this.openDate = openDate;
        this.ceoName = ceoName;
        this.verifyMethod = verifyMethod;
        this.status = status;
        this.verifiedAt = verifiedAt;
    }

    // 국세청 API로 인증이 끝난 기록 (가입 때 사업자 확인 토큰을 가진 회원)
    public static BusinessVerification approvedByApi(Long memberId, String businessNumber,
                                                     LocalDate openDate, String ceoName,
                                                     LocalDateTime verifiedAt) {
        return new BusinessVerification(null, memberId, businessNumber, openDate, ceoName,
                VerifyMethod.AUTO, BusinessVerifyStatus.APPROVED, verifiedAt);
    }

}
