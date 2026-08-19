package com.travelapp.notifications.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Builder
public class DeviceToken {
    private UUID id;
    private UUID userId;
    private String platform;
    private String fcmToken;
    private String deviceModel;
    private String appVersion;
    private boolean active;
    private OffsetDateTime registeredAt;
    private OffsetDateTime lastSeenAt;
}
