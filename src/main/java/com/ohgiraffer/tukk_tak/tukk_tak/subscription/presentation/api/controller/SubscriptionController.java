package com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.SubscriptionInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.usecase.SubscriptionQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.common.PlanResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.common.PlanResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.response.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * comment.
 *  구독 API 컨트롤러 (/subscriptions)
 *  로그인한 회원만 호출할 수 있고(SecurityConfig의 anyRequest().authenticated()), 대상 회원은 토큰에서 꺼낸다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionQueryUsecase subscriptionQueryUsecase;

    /*
     * comment.
     *  내 구독 조회 API
     *  현재 플랜과 이용 기간, 대기 중인 구독, 플랜 한도와 사용량을 보여준다.
     * */

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        SubscriptionInfoResult result = subscriptionQueryUsecase.getMySubscription(principal.getId());

        return ResponseEntity.ok(ApiResponse.success(PlanResponseCode.SUBSCRIPTION_FETCHED,
                PlanResponseMessage.SUBSCRIPTION_FETCHED, SubscriptionResponse.from(result)));
    }

}
