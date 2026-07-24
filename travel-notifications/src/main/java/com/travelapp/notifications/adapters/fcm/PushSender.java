package com.travelapp.notifications.adapters.fcm;

import java.util.Map;

public interface PushSender {
    void send(String fcmToken, String title, String body, Map<String, String> data);
}
