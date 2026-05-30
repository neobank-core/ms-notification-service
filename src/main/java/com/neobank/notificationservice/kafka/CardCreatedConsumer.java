package com.neobank.notificationservice.kafka;

import com.neobank.notificationservice.event.CardCreatedEvent;
import com.neobank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardCreatedConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "card.created",
            groupId = "notification-service-group",
            containerFactory = "cardCreatedKafkaListenerContainerFactory"
    )
    public void onCardCreated(CardCreatedEvent event) {
        log.info("Received card.created for user: {}", event.userId());
        notificationService.createNotification(
                event.userId(),
                "Your card is ready!",
                "Your card " + event.cardNumberMasked() + " is now active"
        );
    }
}
