package com.neobank.notificationservice.event;

import java.util.UUID;

public record AccountCreatedEvent(
        UUID accountId,
        UUID userId,
        String iban
) {
}
