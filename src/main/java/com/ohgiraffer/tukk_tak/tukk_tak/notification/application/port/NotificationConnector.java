package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationConnector {
    SseEmitter connect(Long memberId);
}
