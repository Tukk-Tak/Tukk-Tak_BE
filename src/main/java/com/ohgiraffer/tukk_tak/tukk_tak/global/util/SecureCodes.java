package com.ohgiraffer.tukk_tak.tukk_tak.global.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/*
 * comment.
 *  인증번호와 토큰을 만들고 비교하는 유틸
 *  예측할 수 없어야 하므로 SecureRandom을 쓴다 (Random은 예측 가능성이 있다).
 *  회원가입과 비밀번호 재설정이 같은 방식을 쓰도록 한곳에 모았다.
 * */

public final class SecureCodes {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SecureCodes() {}

    // 6자리 숫자 (앞이 0이어도 6자리를 유지: 000123)
    public static String sixDigitCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    // 예측할 수 없는 토큰: SecureRandom 32바이트를 URL에 안전한 문자열로 변환 (43자)
    public static String urlSafeToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 두 값을 비교할 때 일치하는 자릿수에 따라 걸리는 시간이 달라지지 않도록 MessageDigest.isEqual을 쓴다
    public static boolean matches(String saved, String input) {
        return MessageDigest.isEqual(
                saved.getBytes(StandardCharsets.UTF_8),
                input.getBytes(StandardCharsets.UTF_8));
    }

}
