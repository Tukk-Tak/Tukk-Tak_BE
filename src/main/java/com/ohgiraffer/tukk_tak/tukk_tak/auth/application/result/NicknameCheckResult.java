package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

/*
 * comment.
 *  닉네임 중복 확인 결과
 * */

public record NicknameCheckResult(
        boolean available    // true = 사용 가능, false = 이미 사용 중
) {
}
