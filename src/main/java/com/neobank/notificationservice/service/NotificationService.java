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

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService preferenceService;

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter createSseEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(60L * 1000L * 30L); // 30 mins timeout
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        return emitter;
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }


    public void createNotification(String userId, String subject, String body) {
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

        // Send via SSE
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    // Send JSON representation of notification
                    emitter.send(SseEmitter.event().name("NOTIFICATION").data(
                        String.format("{\"id\":\"%s\",\"subject\":\"%s\",\"body\":\"%s\",\"createdAt\":\"%s\"}",
                                notification.getId(), notification.getSubject(), notification.getBody(), notification.getCreatedAt())
                    ));
                } catch (IOException e) {
                    emitter.complete();
                    removeEmitter(userId, emitter);
                }
            }
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

    @Transactional
    public void markAllAsRead(String userId) {
        int updatedCount = notificationRepository.markAllAsReadByUserId(userId);
        log.info("Marked {} notifications as read for user {}", updatedCount, userId);
    }
}
