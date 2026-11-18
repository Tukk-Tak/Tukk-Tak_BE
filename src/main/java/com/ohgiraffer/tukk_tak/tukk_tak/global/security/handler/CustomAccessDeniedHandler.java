package com.ohgiraffer.tukk_tak.tukk_tak.global.security.handler;

import tools.jackson.databind.ObjectMapper;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * comment.
 *  로그인은 했지만 권한이 부족할 때의 403 응답 생성
 * */

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ApiErrorResponse.of(HttpStatus.FORBIDDEN.value(),
                        ApiResponseCode.FORBIDDEN, ApiResponseMessage.FORBIDDEN));
    }

}
