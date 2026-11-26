package com.ohgiraffer.tukk_tak.tukk_tak.site.presentation;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.command.SaveSiteCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.request.SaveSiteRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response.SiteResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response.SiteResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.site.presentation.response.SaveSiteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/site")
public class SiteController {

    private final SiteCommandUsecase siteCommandUsecase;

    @PutMapping("/save")
    public ResponseEntity<ApiResponse<SaveSiteResponse>> save(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @RequestBody @Valid SaveSiteRequest request
    ){

        SaveSiteResponse result =SaveSiteResponse.from(
                siteCommandUsecase.saveSite(
                        new SaveSiteCommand(customUserPrincipal.getId(), request.siteId(),
                                        request.title(),request.theme(),request.rows())));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        SiteResponseCode.SAVE_SUCCESS,
                        SiteResponseMessage.SAVE_SUCCESS,
                        result
                ));
    }
}
