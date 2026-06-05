package com.neobank.notificationservice.service;

import com.neobank.notificationservice.dto.NotificationPreferenceDto;
import com.neobank.notificationservice.entity.NotificationPreference;
import com.neobank.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;

    public NotificationPreferenceDto getPreferences(String userId) {
        NotificationPreference preference = repository.findById(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        return NotificationPreferenceDto.builder()
                .emailEnabled(preference.isEmailEnabled())
                .smsEnabled(preference.isSmsEnabled())
                .build();
    }

    public NotificationPreferenceDto updatePreferences(String userId, NotificationPreferenceDto dto) {
        NotificationPreference preference = repository.findById(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        preference.setEmailEnabled(dto.isEmailEnabled());
        preference.setSmsEnabled(dto.isSmsEnabled());
        preference.setUpdatedAt(LocalDateTime.now());

        NotificationPreference saved = repository.save(preference);

        return NotificationPreferenceDto.builder()
                .emailEnabled(saved.isEmailEnabled())
                .smsEnabled(saved.isSmsEnabled())
                .build();
    }

    public boolean isEmailEnabled(String userId) {
        return repository.findById(userId)
                .map(NotificationPreference::isEmailEnabled)
                .orElse(true);
    }

    private NotificationPreference createDefaultPreferences(String userId) {
        return repository.save(NotificationPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(true)
                .updatedAt(LocalDateTime.now())
                .build());
    }
}
