package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

/*
 * comment.
 *  결제 상태 ENUM
 *  실패한 결제는 저장하지 않기로 해서 PAID만 있다. (DB 컬럼의 FAILED는 쓰지 않는다)
 * */

public enum PaymentStatus {
    PAID,       // 결제 완료
}
