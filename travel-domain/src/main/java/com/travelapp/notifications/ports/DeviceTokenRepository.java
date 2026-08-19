package com.travelapp.notifications.ports;

import com.travelapp.notifications.domain.DeviceToken;

import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository {
    DeviceToken save(DeviceToken token);
    Optional<DeviceToken> findByUserIdAndPlatform(UUID userId, String platform);
    void deactivateByUserIdAndPlatform(UUID userId, String platform);
}
