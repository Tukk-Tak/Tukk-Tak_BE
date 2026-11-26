package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

/*
 * comment.
 *  결제 상태 ENUM
 *  실패한 결제는 저장하지 않기로 해서 성공(PAID)과, 성공한 뒤 취소된 결제(CANCELED)만 있다.
 *  취소는 토스 상점 관리자에서 수동으로 한 경우 웹훅으로 반영한다. (DB 컬럼의 FAILED는 쓰지 않는다)
 * */

public enum PaymentStatus {
    PAID,       // 결제 완료
    CANCELED    // 취소됨
}
