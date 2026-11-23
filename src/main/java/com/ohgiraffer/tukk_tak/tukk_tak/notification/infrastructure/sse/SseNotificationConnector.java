package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.sse;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port.NotificationConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseNotificationConnector implements NotificationConnector {

    private final SseEmitterStore emitterStore;

    @Override
    public SseEmitter connect(Long memberId) {
        SseEmitter emitter = emitterStore.add(memberId);

        try {
            // 연결 확인용
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (Exception e) {
            emitterStore.remove(memberId, emitter);
            log.warn("SSE 연결 확인 이벤트 전송 실패: memberId={}", memberId, e);
        }

        return emitter;
    }
}