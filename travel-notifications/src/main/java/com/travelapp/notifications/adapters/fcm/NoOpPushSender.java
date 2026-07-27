package com.travelapp.notifications.adapters.fcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "notifications.push.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpPushSender implements PushSender {

    @Override
    public void send(String fcmToken, String title, String body, Map<String, String> data) {
        log.debug("push.noop — FCM not configured. title={}", title);
    }
}
