package com.travelapp.notifications.adapters.fcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "notifications.push.enabled", havingValue = "true")
public class FCMAdapter implements PushSender {

    @Override
    public void send(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("push.skip — no FCM token");
            return;
        }
        log.info("push.sent token={}... title={}",
            fcmToken.substring(0, Math.min(8, fcmToken.length())), title);
    }
}
