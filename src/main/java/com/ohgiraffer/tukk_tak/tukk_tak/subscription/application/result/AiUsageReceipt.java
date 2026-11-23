package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result;

import java.time.LocalDate;

/*
 * comment.
 *  AI 사용 영수증: recordAiUsage가 올린 횟수를 AI 호출이 실패했을 때 정확히 되돌리기 위한 값
 *  구독 id와 주기 시작일이 같아야 "올렸던 그 주기의 횟수"만 깎는다.
 * */

public record AiUsageReceipt(
        Long subscriptionId,    // 횟수를 올린 구독
        LocalDate cycleStart    // 횟수를 올린 주기의 시작일
) {
}
