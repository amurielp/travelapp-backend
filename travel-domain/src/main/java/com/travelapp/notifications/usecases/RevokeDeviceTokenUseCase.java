package com.travelapp.notifications.usecases;

import com.travelapp.notifications.ports.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class RevokeDeviceTokenUseCase {

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void execute(UUID userId, String platform) {
        deviceTokenRepository.deactivateByUserIdAndPlatform(userId, platform);
    }
}
