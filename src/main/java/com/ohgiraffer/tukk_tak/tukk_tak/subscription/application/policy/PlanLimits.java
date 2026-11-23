package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

/*
 * comment.
 *  플랜별 한도 (정책 문서의 수치를 한곳에 모아둔 값). 한도를 바꿀 때는 여기만 고친다.
 *  FREE의 "수정 불가"가 에디터 수정 전체인지 발행 후 수정인지는 팀이 정하므로, 가드는 canEdit 플래그만 가진다.
 * */

public record PlanLimits(
        int aiUsageLimit,       // 한 주기에 쓸 수 있는 AI 횟수
        boolean aiResets,       // true면 월 주기로 리셋, false면 평생 한도 (FREE)
        int publishLimit,       // 동시에 배포할 수 있는 사이트 수 (0이면 배포 불가)
        int saveLimit,          // 저장할 수 있는 사이트 수
        boolean canEdit         // 수정 가능 여부
) {
    private static final PlanLimits FREE = new PlanLimits(1, false, 0, 3, false);
    private static final PlanLimits BASIC = new PlanLimits(3, true, 1, 5, true);
    private static final PlanLimits PREMIUM = new PlanLimits(7, true, 3, 10, true);

    public static PlanLimits of(PlanName planName) {
        return switch (planName) {
            case FREE -> FREE;
            case BASIC -> BASIC;
            case PREMIUM -> PREMIUM;
        };
    }
}
