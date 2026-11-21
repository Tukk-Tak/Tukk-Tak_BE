package com.ohgiraffer.tukk_tak.tukk_tak.global.util;

/*
 * comment.
 *  사업자등록번호 정규화 유틸
 *  하이픈이나 공백이 섞여 들어와도 숫자 10자리로 통일해서 중복 확인, 국세청 호출, 저장에 같은 값을 쓴다.
 *  (123-45-67890 과 1234567890 이 서로 다른 번호로 취급되는 것을 막는다)
 * */

public final class BusinessNumberNormalizer {

    // 유틸 클래스라 객체를 만들 수 없게 막는다
    private BusinessNumberNormalizer() {
    }

    // 숫자가 아닌 문자를 모두 제거한다.
    // 형식(숫자 10자리) 검증은 요청 DTO의 @Pattern에서 이미 끝났으므로 여기서는 하지 않는다.
    public static String normalize(String businessNumber) {
        // \D = 숫자가 아닌 모든 문자 (하이픈, 공백 등)
        return businessNumber.replaceAll("\\D", "");
    }

}
