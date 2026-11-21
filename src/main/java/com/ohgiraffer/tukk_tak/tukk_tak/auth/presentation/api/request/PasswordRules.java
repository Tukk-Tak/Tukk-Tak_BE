package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

/*
 * comment.
 *  비밀번호 형식 규칙 (회원가입, 비밀번호 재설정, 이후 비밀번호 변경이 같은 규칙을 쓴다)
 * */

public final class PasswordRules{

    private PasswordRules() {}

    // 영문, 숫자, 특수문자를 모두 포함한 8~16자 (공백 불가)
    public static final String REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,16}$";
    public static final String MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자로 입력해주세요.";

}
