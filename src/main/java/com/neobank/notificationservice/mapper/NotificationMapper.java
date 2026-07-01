package com.neobank.notificationservice.mapper;

import org.mapstruct.Mapper;
import com.neobank.notificationservice.dto.NotificationResponse;
import com.neobank.notificationservice.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
