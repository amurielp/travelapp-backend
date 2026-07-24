package com.travelapp.notifications.repository;

import com.travelapp.notifications.domain.NotificationType;

import java.util.UUID;

public interface NotificationLogRepository {
    void save(UUID userId, NotificationType type, String channel, String title, String body);
    void save(UUID userId, NotificationType type, String channel, String title, String body,
              UUID tripId, UUID eventId, UUID gapId);
}
