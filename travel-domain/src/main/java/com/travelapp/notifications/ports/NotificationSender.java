package com.travelapp.notifications.ports;

import com.travelapp.notifications.domain.NotificationType;
import java.util.Map;
import java.util.UUID;

public interface NotificationSender {
    void sendPush(UUID userId, String title, String body, Map<String, String> data);
    void sendEmail(UUID userId, String subject, String templateId, Map<String, Object> vars);
    void sendInApp(UUID userId, NotificationType type, String title, String body, UUID tripId, UUID eventId, UUID gapId);
}
