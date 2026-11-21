package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model;

/*
 * comment.
 *  사업자 인증 상태 ENUM
 *  지금은 APPROVED만 저장하고, 나머지는 DDL과 맞추려고 둔다.
 * */

public enum BusinessVerifyStatus {
    PENDING,
    APPROVED,
    REJECTED,
    ERROR
}
