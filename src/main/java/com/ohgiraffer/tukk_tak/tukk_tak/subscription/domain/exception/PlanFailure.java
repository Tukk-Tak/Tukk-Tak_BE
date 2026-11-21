package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.exception;

/*
 * comment.
 *  구독 플랜 한도 위반 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 PlanExceptionHandler가 변환한다.
 * */

public enum PlanFailure {
    AI_LIMIT_EXCEEDED,          // 이번 주기 AI 사용 횟수를 모두 씀
    SAVE_LIMIT_EXCEEDED,        // 저장 가능한 사이트 수를 넘김
    PUBLISH_LIMIT_EXCEEDED,     // 배포 가능한 사이트 수를 넘김
    PUBLISH_NOT_ALLOWED,        // 현재 플랜에서는 배포 불가 (FREE)
    EDIT_NOT_ALLOWED,           // 현재 플랜에서는 수정 불가
    BUSINESS_NOT_VERIFIED       // 사업자 인증을 하지 않아 배포 불가
}
