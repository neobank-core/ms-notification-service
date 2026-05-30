package com.neobank.notificationservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String subject,
        String body,
        String type,
        String status,
        LocalDateTime createdAt
) {}