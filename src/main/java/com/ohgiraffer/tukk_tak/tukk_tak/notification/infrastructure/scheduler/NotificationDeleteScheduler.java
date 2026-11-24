package com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.scheduler;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.infrastructure.persistence.SpringDataNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDeleteScheduler {

    private final SpringDataNotificationRepository springDataNotificationRepository;
    private static final int RETENTION_DAYS = 7;

    // 매일 새벽 3시에 7일 지난 알림 삭제
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpiredNotifications() {
        try {
            int deleted = springDataNotificationRepository
                    .deleteByCreatedAtBefore(LocalDateTime.now().minusDays(RETENTION_DAYS));
            log.info("만료 알림 삭제 완료: {}건", deleted);
        } catch (Exception e) {
            log.error("만료 알림 삭제 실패", e);
        }
    }
}