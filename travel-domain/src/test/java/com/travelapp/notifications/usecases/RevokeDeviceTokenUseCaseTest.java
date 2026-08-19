package com.travelapp.notifications.usecases;

import com.travelapp.notifications.ports.DeviceTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RevokeDeviceTokenUseCaseTest {

    @Mock DeviceTokenRepository deviceTokenRepository;
    @InjectMocks RevokeDeviceTokenUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_callsDeactivateWithCorrectUserId() {
        useCase.execute(userId, "ios");

        verify(deviceTokenRepository).deactivateByUserIdAndPlatform(userId, "ios");
    }

    @Test
    void execute_callsDeactivateWithCorrectPlatform() {
        useCase.execute(userId, "android");

        verify(deviceTokenRepository).deactivateByUserIdAndPlatform(userId, "android");
    }

    @Test
    void execute_doesNothingElseBesidesDeactivate() {
        useCase.execute(userId, "ios");

        verify(deviceTokenRepository).deactivateByUserIdAndPlatform(userId, "ios");
        verifyNoMoreInteractions(deviceTokenRepository);
    }
}
