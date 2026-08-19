package com.travelapp.users.usecases;

import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.ports.UserConsentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserConsentUseCaseTest {

    @Mock UserConsentRepository consentRepository;
    @InjectMocks UpdateUserConsentUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final UpdateUserConsentCommand cmd = new UpdateUserConsentCommand(userId, true, true, "2.0");

    @Test
    void execute_savesConsentWithValuesFromCommand() {
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(consentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<UserConsent> captor = ArgumentCaptor.forClass(UserConsent.class);
        useCase.execute(cmd);
        verify(consentRepository).save(captor.capture());

        UserConsent saved = captor.getValue();
        assertThat(saved.isAdsPersonalized()).isTrue();
        assertThat(saved.isAnalytics()).isTrue();
        assertThat(saved.getConsentVersion()).isEqualTo("2.0");
    }

    @Test
    void execute_savedConsentHasConsentedAtNotNull() {
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(consentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<UserConsent> captor = ArgumentCaptor.forClass(UserConsent.class);
        useCase.execute(cmd);
        verify(consentRepository).save(captor.capture());

        assertThat(captor.getValue().getConsentedAt()).isNotNull();
    }

    @Test
    void execute_returnsObjectReturnedBySave() {
        UserConsent persisted = UserConsent.builder()
                .userId(userId).adsPersonalized(true).analytics(true)
                .consentVersion("2.0").consentedAt(OffsetDateTime.now()).build();
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(consentRepository.save(any())).thenReturn(persisted);

        UserConsent result = useCase.execute(cmd);

        assertThat(result).isSameAs(persisted);
    }

    @Test
    void execute_preservesExistingConsentedAt_whenConsentAlreadyExists() {
        OffsetDateTime original = OffsetDateTime.now().minusDays(10);
        UserConsent existing = UserConsent.builder()
                .userId(userId).adsPersonalized(false).analytics(false)
                .consentVersion("1.0").consentedAt(original).build();
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(consentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<UserConsent> captor = ArgumentCaptor.forClass(UserConsent.class);
        useCase.execute(cmd);
        verify(consentRepository).save(captor.capture());

        assertThat(captor.getValue().getConsentedAt()).isEqualTo(original);
    }
}
