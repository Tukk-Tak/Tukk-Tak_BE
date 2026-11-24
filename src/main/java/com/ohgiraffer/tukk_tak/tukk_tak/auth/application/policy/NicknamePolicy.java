package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;

import java.util.regex.Pattern;

/*
 * comment.
 *  닉네임 규칙: 한글(완성형), 영문, 숫자로만 2~15자
 *  공백, 특수문자, 이모지, 자음·모음만 쓴 글자(ㅋㅋ)는 허용하지 않는다.
 *  중복 확인과 회원가입이 같은 규칙을 쓰도록 한 곳에 모았다.
 *  15자는 member.nickname 컬럼(VARCHAR(15))에 맞춘 값이다.
 * */

public class NicknamePolicy {

    private static final Pattern PATTERN = Pattern.compile("[가-힣a-zA-Z0-9]{2,15}");

    private NicknamePolicy() {}

    // 규칙에 맞지 않으면 AuthException.nicknameInvalid()를 던진다 (null이면 요청에 값이 없는 경우)
    public static void validate(String nickname) {
        if (nickname == null || !PATTERN.matcher(nickname).matches()) {
            throw AuthException.nicknameInvalid();
        }
    }

}
