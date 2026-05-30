package com.neobank.notificationservice.service;

import com.neobank.notificationservice.entity.Notification;
import com.neobank.notificationservice.enums.NotificationStatus;
import com.neobank.notificationservice.enums.NotificationType;
import com.neobank.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(String userId, String subject, String body) {
        Notification notification = Notification.builder().
                userId(userId)
                .type(NotificationType.IN_APP)
                .subject(subject)
                .body(body)
                .status(NotificationStatus.SENT)
                .build();
        notificationRepository.save(notification);
        log.info("Notification saved for user {}: {}", userId, subject);
    }

    public Page<Notification> getMyNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    public void markAsRead(UUID notificationId, String userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUserId().equals(userId)) {
                notification.setStatus(NotificationStatus.READ);
                notificationRepository.save(notification);
            }
        });
    }
}
