package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.WithdrawalCommand;

/*
 * comment.
 *  회원 탈퇴 기능 목록
 * */

public interface WithdrawalUsecase {

    // 회원 탈퇴: 본인 확인 후 탈퇴 처리하고, 모든 기기의 로그인을 끊는다
    void withdraw(WithdrawalCommand command);

}
