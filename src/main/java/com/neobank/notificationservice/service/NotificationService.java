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
    private final NotificationPreferenceService preferenceService;


    public void createNotification(String userId, String subject, String body) {
        // 1. In-App уведомления мы сохраняем всегда (их нельзя отключить)
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.IN_APP)
                .subject(subject)
                .body(body)
                .status(NotificationStatus.SENT)
                .build();
        notificationRepository.save(notification);
        log.info("In-App Notification saved for user {}: {}", userId, subject);

        if (preferenceService.isEmailEnabled(userId)) {
            log.info("📧 MOCK EMAIL SENT to user {}: Subject='{}', Body='{}'", userId, subject, body);
        } else {
            log.info("🚫 Email skipped for user {} (disabled in preferences)", userId);
        }
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

    public void markAllAsRead(String userId) {
        notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.SENT)
                .forEach(notification -> {
                    notification.setStatus(NotificationStatus.READ);
                    notificationRepository.save(notification);
                });
    }
}
