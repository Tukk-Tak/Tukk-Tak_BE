package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

public interface NotificationRepository {
    Notification save(Notification notification);
}
