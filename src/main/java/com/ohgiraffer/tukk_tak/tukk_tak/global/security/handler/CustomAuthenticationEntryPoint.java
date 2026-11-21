package com.ohgiraffer.tukk_tak.tukk_tak.global.security.handler;

import tools.jackson.databind.ObjectMapper;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * comment.
 *  로그인이 필요한 요청을 로그인 없이 보냈을 때의 401 응답 생성
 *  필터 단계의 오류는 ApiExceptionHandler를 거치지 않아서 여기서 같은 응답 형식으로 직접 쓴다.
 * */

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;   // Boot 4: tools.jackson.databind / Boot 3: com.fasterxml.jackson.databind

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        boolean loggedOut = Boolean.TRUE.equals(request.getAttribute("ALREADY_LOGGED_OUT"));
        String code = loggedOut ? AuthResponseCode.ALREADY_LOGGED_OUT : ApiResponseCode.UNAUTHORIZED;
        String message = loggedOut ? AuthResponseMessage.ALREADY_LOGGED_OUT : ApiResponseMessage.UNAUTHORIZED;

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ApiErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), code, message));
    }

}
