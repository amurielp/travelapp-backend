package com.travelapp.notifications.usecases;

import com.travelapp.notifications.domain.DeviceToken;
import com.travelapp.notifications.ports.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class RegisterDeviceTokenUseCase {

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public DeviceToken execute(RegisterDeviceTokenCommand cmd) {
        deviceTokenRepository.deactivateByUserIdAndPlatform(cmd.userId(), cmd.platform());
        return deviceTokenRepository.save(DeviceToken.builder()
            .id(UUID.randomUUID())
            .userId(cmd.userId())
            .platform(cmd.platform())
            .fcmToken(cmd.fcmToken())
            .deviceModel(cmd.deviceModel())
            .appVersion(cmd.appVersion())
            .active(true)
            .registeredAt(OffsetDateTime.now())
            .lastSeenAt(OffsetDateTime.now())
            .build());
    }
}
