package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model;

/*
 * comment.
 *  이메일 인증번호를 쓰는 용도
 *  같은 이메일이라도 용도가 다르면 Redis 키가 달라서, 가입용 번호와 토큰으로 비밀번호를 재설정할 수 없다.
 * */

public enum EmailPurpose{
        SIGNUP,             // 회원가입
        PASSWORD_RESET      // 비밀번호 재설정
}
