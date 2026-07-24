package com.travelapp.notifications.adapters;

import com.travelapp.notifications.adapters.email.EmailAdapter;
import com.travelapp.notifications.adapters.fcm.PushSender;
import com.travelapp.notifications.domain.NotificationType;
import com.travelapp.notifications.ports.NotificationSender;
import com.travelapp.notifications.repository.NotificationLogRepository;
import com.travelapp.notifications.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Implementación principal del puerto NotificationSender.
 * Orquesta FCM + Email + persistencia en notification_log.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSenderImpl implements NotificationSender {

    private final PushSender              pushSender;
    private final EmailAdapter            emailAdapter;
    private final UserTokenRepository     tokens;    // obtiene FCM token del usuario
    private final NotificationLogRepository logRepo; // persiste en notification_log

    @Override
    public void sendPush(UUID userId, String title, String body, Map<String, String> data) {
        var token = tokens.findFcmToken(userId).orElse(null);
        if (token != null) {
            pushSender.send(token, title, body, data);
        }
        logRepo.save(userId, NotificationType.valueOf(
            data.getOrDefault("type", "GAP_DETECTED")), "push", title, body);
    }

    @Override
    public void sendEmail(UUID userId, String subject, String templateId, Map<String, Object> vars) {
        var email = tokens.findEmail(userId).orElse(null);
        if (email != null) {
            emailAdapter.send(email, subject, templateId, vars);
        }
        logRepo.save(userId, NotificationType.GAP_DETECTED, "email", subject, "");
    }

    @Override
    public void sendInApp(UUID userId, NotificationType type, String title, String body,
                          UUID tripId, UUID eventId, UUID gapId) {
        logRepo.save(userId, type, "in_app", title, body, tripId, eventId, gapId);
        log.info("inapp.saved userId={} type={} title={}", userId, type, title);
    }
}
