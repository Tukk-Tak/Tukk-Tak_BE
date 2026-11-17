package com.ohgiraffer.tukk_tak.tukk_tak.notification.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.model.Notification;

public interface NotificationSender {
    void send(Notification notification);

}
