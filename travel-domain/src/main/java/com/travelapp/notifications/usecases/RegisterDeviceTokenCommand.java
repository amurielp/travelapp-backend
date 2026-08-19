package com.travelapp.notifications.usecases;

import java.util.UUID;

public record RegisterDeviceTokenCommand(
    UUID userId,
    String platform,
    String fcmToken,
    String deviceModel,
    String appVersion
) {}
