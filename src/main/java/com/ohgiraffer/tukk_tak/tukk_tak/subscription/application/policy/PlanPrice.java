package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

/*
 * comment.
 *  플랜별 1개월 가격 (원). 가격을 바꿀 때는 여기만 고친다.
 *  결제 금액은 프론트가 보낸 값을 믿지 않고 항상 "1개월 가격 x 개월 수"로 서버가 계산한다.
 * */

public record PlanPrice(
        int monthlyPrice        // 1개월 이용 가격 (원)
) {
    private static final PlanPrice BASIC = new PlanPrice(9_900);
    private static final PlanPrice PREMIUM = new PlanPrice(19_900);

    // FREE는 판매하는 상품이 아니라 가격이 없다 (호출하기 전에 막아야 하는 코드 버그)
    public static PlanPrice of(PlanName planName) {
        return switch (planName) {
            case FREE -> throw new IllegalArgumentException("FREE 플랜은 가격이 없습니다.");
            case BASIC -> BASIC;
            case PREMIUM -> PREMIUM;
        };
    }
}
