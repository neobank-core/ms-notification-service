package com.neobank.notificationservice.mapper;

import com.neobank.notificationservice.dto.NotificationResponse;
import com.neobank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getSubject(),
                notification.getBody(),
                notification.getType().name(),
                notification.getStatus().name(),
                notification.getCreatedAt()
        );
    }
}
