package com.neobank.notificationservice.kafka;

import com.neobank.notificationservice.event.UserRegisteredEvent;
import com.neobank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "user.registered",
            groupId = "notification-service-group",
            containerFactory = "userRegisteredKafkaListenerContainerFactory"
    )
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Received user.registered for: {}", event.keycloakUserId());
        notificationService.createNotification(
                event.keycloakUserId(),
                "Welcome to NeoBank!",
                "Hi " + event.firstName() + ", welcome to NeoBank!"
        );
    }
}
