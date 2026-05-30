package com.neobank.notificationservice.kafka;

import com.neobank.notificationservice.event.TransactionCompletedEvent;
import com.neobank.notificationservice.event.TransactionFailedEvent;
import com.neobank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "transaction.completed",
            groupId = "notification-service-group",
            containerFactory = "transactionCompletedKafkaListenerContainerFactory"
    )
    public void onTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Received transaction.completed for user: {}", event.keycloakUserId());
        notificationService.createNotification(
                event.keycloakUserId(),
                "Transfer successful",
                "You sent " + event.amount() + " " + event.currency()
        );
    }

    @KafkaListener(
            topics = "transaction.failed",
            groupId = "notification-service-group",
            containerFactory = "transactionFailedKafkaListenerContainerFactory"
    )
    public void onTransactionFailed(TransactionFailedEvent event) {
        log.info("Received transaction.failed for transaction: {}", event.transactionId());
    }
}
