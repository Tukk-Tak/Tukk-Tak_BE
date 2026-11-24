package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationConnectionUsecase {
    SseEmitter makeConnection(Long memberId);
}
