package com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationConnectionUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.GetNotificationList;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.NotificationResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.NotificationResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationConnectionUsecase notificationConnectionUsecase;
    private final NotificationQueryUsecase notificationQueryUsecase;


    @GetMapping("/sse/connect")
    public ResponseEntity<SseEmitter> sseConnection(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        SseEmitter emitter = notificationConnectionUsecase.makeConnection(customUserPrincipal.getId());

        return ResponseEntity.ok()
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<GetNotificationList>>> getNotificationList(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        List<GetNotificationList> result = notificationQueryUsecase.getNotificationList(customUserPrincipal.getId()).stream()
                .map(GetNotificationList::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        NotificationResponseCode.ANSWER_FOUND,
                        NotificationResponseMessage.ANSWER_FOUND,
                        result
                ));
    }

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> isUnreadNotificationExists(
        @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        NotificationResponseCode.ANSWER_FOUND,
                        NotificationResponseMessage.ANSWER_FOUND,
                        notificationQueryUsecase.getIsUnreadNotificationExists(customUserPrincipal.getId())
                ));
    }
}
