package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;

/*
 * comment.
 *  로컬 개발용 가짜 구글 토큰 검증기 (구글 클라이언트 ID가 없을 때만 local에서 사용)
 *  credential 형식: "fake:이메일:이름" 이면 정상, "fake-unverified:이메일" 이면 이메일 미인증, 그 외는 토큰 오류
 * */

public class FakeGoogleTokenVerifier implements GoogleTokenVerifier {

    @Override
    public GoogleIdentity verify(String credential) {
        if (credential != null && credential.startsWith("fake-unverified:")) {
            throw AuthException.googleEmailUnverified();
        }
        if (credential == null || !credential.startsWith("fake:")) {
            throw AuthException.googleTokenInvalid();
        }
        String[] parts = credential.split(":", 3);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw AuthException.googleTokenInvalid();
        }
        return new GoogleIdentity(parts[1], parts.length == 3 ? parts[2] : "");
    }

}
