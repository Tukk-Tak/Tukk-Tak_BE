package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.NicknameCheckResult;

/*
 * comment.
 *  닉네임 중복 확인 응답
 * */

public record NicknameCheckResponse(
        boolean available
) {
    public static NicknameCheckResponse from(NicknameCheckResult result) {
        return new NicknameCheckResponse(result.available());
    }
}
