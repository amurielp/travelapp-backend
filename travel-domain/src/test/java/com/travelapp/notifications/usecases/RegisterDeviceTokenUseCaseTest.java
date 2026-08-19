package com.travelapp.notifications.usecases;

import com.travelapp.notifications.domain.DeviceToken;
import com.travelapp.notifications.ports.DeviceTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterDeviceTokenUseCaseTest {

    @Mock DeviceTokenRepository deviceTokenRepository;
    @InjectMocks RegisterDeviceTokenUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final RegisterDeviceTokenCommand cmd = new RegisterDeviceTokenCommand(
            userId, "android", "fcm-token-abc", "Pixel 8", "2.0.0");

    @Test
    void execute_registersNewToken_whenNoPreviousTokenForPlatform() {
        DeviceToken saved = DeviceToken.builder()
                .id(UUID.randomUUID()).userId(userId).platform("android")
                .fcmToken("fcm-token-abc").active(true).build();
        when(deviceTokenRepository.save(any())).thenReturn(saved);

        DeviceToken result = useCase.execute(cmd);

        verify(deviceTokenRepository).deactivateByUserIdAndPlatform(userId, "android");
        assertThat(result.getFcmToken()).isEqualTo("fcm-token-abc");
    }

    @Test
    void execute_deactivatesPreviousToken_beforeSavingNew() {
        when(deviceTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(cmd);

        verify(deviceTokenRepository).deactivateByUserIdAndPlatform(userId, "android");
        verify(deviceTokenRepository).save(any(DeviceToken.class));
    }

    @Test
    void execute_savedTokenHasActiveTrue() {
        when(deviceTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
        useCase.execute(cmd);
        verify(deviceTokenRepository).save(captor.capture());

        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    void execute_savedTokenCarriesCorrectUserId() {
        when(deviceTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
        useCase.execute(cmd);
        verify(deviceTokenRepository).save(captor.capture());

        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }
}
