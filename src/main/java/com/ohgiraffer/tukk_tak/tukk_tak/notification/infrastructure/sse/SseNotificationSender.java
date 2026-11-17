package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.sse;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port.NotificationSender;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;
import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.dto.NotificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseNotificationSender implements NotificationSender {

    private final SseEmitterStore emitterStore;

    @Override
    public void send(Notification notification) {
        Long memberId = notification.getMemberId();
        NotificationResult payload = NotificationResult.from(notification);

        for (SseEmitter emitter : emitterStore.get(memberId)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (Exception e) {
                // 끊긴 연결 정리
                emitterStore.remove(memberId, emitter);
                log.warn("SSE 전송 실패: memberId={}", memberId, e);
            }
        }
    }
}