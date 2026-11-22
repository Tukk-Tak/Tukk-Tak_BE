package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase.MemberBusinessUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase.MemberProfileUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.common.MemberResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.common.MemberResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.request.MemberBusinessVerifyRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.request.MemberUpdateRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
 * comment.
 *  마이페이지 회원 정보 API 컨트롤러 (/members/me)
 *  로그인한 회원만 호출할 수 있고(SecurityConfig의 anyRequest().authenticated()), 대상 회원은 토큰에서 꺼낸다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me")
public class MemberController {

    private final MemberProfileUsecase memberProfileUsecase;
    private final MemberBusinessUsecase memberBusinessUsecase;

    /*
     * comment.
     *  내 정보 조회
     *  현재 인증된 사업자가 있으면 business에 함께 내려준다.
     * */
    @GetMapping
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        MemberInfoResult result = memberProfileUsecase.getMyInfo(principal.getId());

        return ResponseEntity.ok(ApiResponse.success(MemberResponseCode.MY_INFO_FETCHED,
                MemberResponseMessage.MY_INFO_FETCHED, MemberResponse.from(result)));
    }

    /*
     * comment.
     *  내 정보 수정
     *  보낸 항목만 바꾸고, 수정된 전체 정보를 조회와 같은 형태로 돌려준다.
     * */
    @PatchMapping
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyInfo(
            @RequestBody @Valid MemberUpdateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        MemberInfoResult result = memberProfileUsecase.updateMyInfo(request.toCommand(principal.getId()));

        return ResponseEntity.ok(ApiResponse.success(MemberResponseCode.MY_INFO_UPDATED,
                MemberResponseMessage.MY_INFO_UPDATED, MemberResponse.from(result)));
    }

    /*
     * comment.
     *  사업자 인증 (최초 인증과 수정 겸용)
     *  국세청 진위확인을 통과하면 현재 사업자로 바꾸고, 실패하면 기존 인증은 그대로 유지된다.
     *  대표자명은 받지 않고 회원 이름으로 확인한다.
     * */
    @PostMapping("/business/verify")
    public ResponseEntity<ApiResponse<MemberResponse.BusinessResponse>> verifyMyBusiness(
            @RequestBody @Valid MemberBusinessVerifyRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        MemberInfoResult.BusinessInfo result = memberBusinessUsecase.verifyMyBusiness(
                request.toCommand(principal.getId()));

        return ResponseEntity.ok(ApiResponse.success(MemberResponseCode.BUSINESS_VERIFIED,
                MemberResponseMessage.BUSINESS_VERIFIED, MemberResponse.BusinessResponse.from(result)));
    }

}
