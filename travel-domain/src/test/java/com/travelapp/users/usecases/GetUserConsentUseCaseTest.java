package com.travelapp.users.usecases;

import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.ports.UserConsentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserConsentUseCaseTest {

    @Mock UserConsentRepository consentRepository;
    @InjectMocks GetUserConsentUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_returnsConsentFromRepo_whenExists() {
        UserConsent existing = UserConsent.builder()
                .userId(userId).adsPersonalized(true).analytics(true)
                .consentVersion("2.0").consentedAt(OffsetDateTime.now()).build();
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        UserConsent result = useCase.execute(userId);

        assertThat(result.isAdsPersonalized()).isTrue();
        assertThat(result.isAnalytics()).isTrue();
        assertThat(result.getConsentVersion()).isEqualTo("2.0");
    }

    @Test
    void execute_returnsEmptyConsent_whenNotExistsInRepo() {
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserConsent result = useCase.execute(userId);

        assertThat(result.isAdsPersonalized()).isFalse();
        assertThat(result.isAnalytics()).isFalse();
    }

    @Test
    void execute_emptyConsentHasVersion10() {
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserConsent result = useCase.execute(userId);

        assertThat(result.getConsentVersion()).isEqualTo("1.0");
    }

    @Test
    void execute_emptyConsentBelongsToRequestedUser() {
        when(consentRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserConsent result = useCase.execute(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
    }
}
