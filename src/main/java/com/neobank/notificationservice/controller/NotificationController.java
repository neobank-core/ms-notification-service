package com.neobank.notificationservice.controller;

import com.neobank.notificationservice.dto.NotificationResponse;
import com.neobank.notificationservice.entity.Notification;
import com.neobank.notificationservice.mapper.NotificationMapper;
import com.neobank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(jwt.getSubject(), pageable)
                        .map(notificationMapper::toResponse));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        notificationService.markAsRead(id, jwt.getSubject());
        return ResponseEntity.ok().build();
    }
}