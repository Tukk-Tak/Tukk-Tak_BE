package com.ohgiraffer.tukk_tak.tukk_tak.global.util;

import java.util.Locale;

/*
 * comment.
 *  이메일 정규화 유틸 (앞뒤 공백 제거 + 소문자)
 *  로그인, 이메일 인증, 회원가입이 모두 같은 규칙을 써야
 *  "Abc@x.com으로 인증하고 abc@x.com으로 가입" 같은 불일치가 생기지 않는다.
 * */

public final class EmailNormalizer {

    private EmailNormalizer() {}

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
