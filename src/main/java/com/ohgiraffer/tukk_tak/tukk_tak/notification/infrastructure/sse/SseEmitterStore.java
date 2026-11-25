package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterStore {

    // sse 연결 타임아웃 30분
    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long memberId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);

        emitters.compute(memberId, (id, list) -> {
            List<SseEmitter> target = list == null ? new CopyOnWriteArrayList<>() : list;
            target.add(emitter);
            return target;
        });

        emitter.onCompletion(() -> remove(memberId, emitter));
        emitter.onTimeout(() -> remove(memberId, emitter));
        emitter.onError(e -> remove(memberId, emitter));
        return emitter;
    }

    public List<SseEmitter> get(Long memberId) {
        return emitters.getOrDefault(memberId, List.of());
    }

    public void remove(Long memberId, SseEmitter emitter) {
        emitters.computeIfPresent(memberId, (id, list) -> {
            list.remove(emitter);
            return list.isEmpty() ? null : list;
        });
    }
}