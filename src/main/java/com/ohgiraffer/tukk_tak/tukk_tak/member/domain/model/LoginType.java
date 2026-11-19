package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model;

/*
 * comment.
 *  가입 경로 ENUM (LOCAL: 자체 가입, GOOGLE: 구글 가입)
 *  가입 경로를 기록하는 용도이며, 로그인 방식을 제한하지는 않는다.
 * */

public enum LoginType {
    LOCAL,
    GOOGLE
}
