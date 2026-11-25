package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port.NotificationConnector;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.usecase.NotificationConnectionUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationConnectionService implements NotificationConnectionUsecase {

    private final NotificationConnector notificationConnector;
    @Override
    public SseEmitter makeConnection(Long memberId) {
        return notificationConnector.connect(memberId);
    }
}
