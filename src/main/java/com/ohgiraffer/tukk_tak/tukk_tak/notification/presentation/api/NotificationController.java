package com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationConnectionUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.GetNotificationList;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.NotificationResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.presentation.api.response.NotificationResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationConnectionUsecase notificationConnectionUsecase;
    private final NotificationQueryUsecase notificationQueryUsecase;
    private final NotificationCommandUsecase notificationCommandUsecase;

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

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> notificationRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        notificationCommandUsecase.markAsRead(notificationId,customUserPrincipal.getId() );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        NotificationResponseCode.NOTIFICATION_READ,
                        NotificationResponseMessage.NOTIFICATION_READ,
                        null
                ));
    }


//    @DeleteMapping("/delete")
//    public ResponseEntity<ApiResponse<Void>> deleteNotification(
//            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
//
//    ){
//
//        notificationCommandUsecase.deleteNotification();
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(
//                        NotificationResponseCode.DELETE_SUCCESS,
//                        NotificationResponseMessage.DELETE_SUCCESS,
//                        null
//                ));
//    }
}
