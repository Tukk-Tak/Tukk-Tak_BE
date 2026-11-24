package com.ohgiraffer.tukk_tak.tukk_tak.global.util;

/*
 * comment.
 *  전화번호 정규화
 *  하이픈과 공백을 제거해서 숫자만 저장한다. (프론트가 하이픈을 붙여서 보여준다)
 *  비어 있거나 null이면 null을 돌려준다. (업장 전화번호 같은 선택 입력값용)
 * */

public final class PhoneNumberNormalizer {

    private PhoneNumberNormalizer() {
    }

    public static String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.replaceAll("\\D", "");
    }

}
