package com.travelapp.persistence.repositories;

import com.travelapp.notifications.domain.NotificationType;
import com.travelapp.notifications.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationLogRepositoryAdapter implements NotificationLogRepository {

    private final JdbcTemplate jdbc;

    @Override
    public void save(UUID userId, NotificationType type, String channel, String title, String body) {
        save(userId, type, channel, title, body, null, null, null);
    }

    @Override
    public void save(UUID userId, NotificationType type, String channel,
                     String title, String body, UUID tripId, UUID eventId, UUID gapId) {
        jdbc.update("""
                INSERT INTO notification_log
                  (id, user_id, trip_id, event_id, gap_id, notification_type, channel, title, body)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID(), userId, tripId, eventId, gapId,
                type.name(), channel, title, body);
    }
}
