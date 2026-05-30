package com.neobank.notificationservice.kafka;

import com.neobank.notificationservice.event.AccountCreatedEvent;
import com.neobank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountCreatedConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "account.created",
            groupId = "notification-service-group",
            containerFactory = "accountCreatedKafkaListenerContainerFactory"
    )
    public void onAccountCreated(AccountCreatedEvent event) {
        log.info("Received account.created for user: {}", event.userId());
        notificationService.createNotification(
                event.userId().toString(),
                "Account opened",
                "Your checking account " + event.iban() + " is ready."
        );
    }
}
