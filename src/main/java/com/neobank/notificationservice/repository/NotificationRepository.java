package com.neobank.notificationservice.repository;

import com.neobank.notificationservice.entity.Notification;
import com.neobank.notificationservice.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserId(String userId, Pageable pageable);

    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Notification n SET n.status = 'READ' WHERE n.userId = :userId AND n.status = 'SENT'")
    int markAllAsReadByUserId(@org.springframework.data.repository.query.Param("userId") String userId);
}
