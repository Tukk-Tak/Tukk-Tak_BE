package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;

/*
 * comment.
 *  구글 ID 토큰 검증 포트(인터페이스)
 *  서비스는 이 인터페이스만 알고, 실제 구글 검증인지 로컬용 가짜인지는 모른다.
 * */

public interface GoogleTokenVerifier {

    // 구글 ID 토큰을 검증해 사용자 정보를 돌려준다 (토큰 오류는 AuthException, 이메일 미인증도 AuthException)
    GoogleIdentity verify(String credential);

}
