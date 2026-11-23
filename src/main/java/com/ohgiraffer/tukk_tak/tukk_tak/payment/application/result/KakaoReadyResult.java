package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

/*
 * comment.
 *  카카오페이 결제 준비(ready) 호출 결과
 *  카카오 응답 형식을 서비스가 몰라도 되도록 필요한 세 값만 담는다.
 *  tid는 승인 단계에서 반드시 필요해서 임시 주문에 같이 저장한다.
 * */

public record KakaoReadyResult(
        String tid,                  // 카카오페이 결제 고유번호
        String redirectPcUrl,        // PC 결제 페이지 주소
        String redirectMobileUrl     // 모바일 웹 결제 페이지 주소
) {
}
