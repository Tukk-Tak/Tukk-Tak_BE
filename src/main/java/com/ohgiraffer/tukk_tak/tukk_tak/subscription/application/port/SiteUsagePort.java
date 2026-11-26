package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.port;

/*
 * comment.
 *  회원의 사이트 사용량 포트
 *  사이트 테이블은 사이트 도메인이 가지고 있어서, 구독 조회는 개수를 이 포트로만 받는다.
 * */

public interface SiteUsagePort {

    // 회원이 저장해 둔 사이트 수
    int countSaved(Long memberId);

    // 회원이 지금 배포 중인 사이트 수
    int countPublished(Long memberId);

}
