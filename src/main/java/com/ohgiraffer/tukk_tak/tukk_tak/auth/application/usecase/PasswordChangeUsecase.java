package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordChangeCommand;

/*
 * comment.
 *  비밀번호 변경 기능 목록 (로그인한 상태에서 현재 비밀번호로 본인을 확인하고 새 비밀번호로 바꾼다)
 * */

public interface PasswordChangeUsecase {

    // 비밀번호 변경: 현재 비밀번호를 확인하고 바꾼 뒤, 모든 기기의 로그인을 끊는다
    void changePassword(PasswordChangeCommand command);

}
