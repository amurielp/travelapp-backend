package com.travelapp.users.usecases;

import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.ports.RewardedUnlockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterRewardedUnlockUseCaseTest {

    @Mock RewardedUnlockRepository unlockRepository;
    @InjectMocks RegisterRewardedUnlockUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_expiresAtIsUnlockedAtPlusTtlHours() {
        when(unlockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RegisterRewardedUnlockCommand cmd = new RegisterRewardedUnlockCommand(userId, "ai_suggestions", 24);

        ArgumentCaptor<RewardedUnlock> captor = ArgumentCaptor.forClass(RewardedUnlock.class);
        useCase.execute(cmd);
        verify(unlockRepository).save(captor.capture());

        RewardedUnlock saved = captor.getValue();
        assertThat(saved.getExpiresAt()).isEqualTo(saved.getUnlockedAt().plusHours(24));
    }

    @Test
    void execute_savedUnlockHasCorrectFeatureKey() {
        when(unlockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RegisterRewardedUnlockCommand cmd = new RegisterRewardedUnlockCommand(userId, "ai_pdf_parsing", 8);

        ArgumentCaptor<RewardedUnlock> captor = ArgumentCaptor.forClass(RewardedUnlock.class);
        useCase.execute(cmd);
        verify(unlockRepository).save(captor.capture());

        assertThat(captor.getValue().getFeatureKey()).isEqualTo("ai_pdf_parsing");
    }

    @Test
    void execute_returnedUnlockHasUserIdFromCommand() {
        when(unlockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RegisterRewardedUnlockCommand cmd = new RegisterRewardedUnlockCommand(userId, "ai_suggestions", 12);

        RewardedUnlock result = useCase.execute(cmd);

        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void execute_returnsObjectFromRepository() {
        RewardedUnlock persisted = RewardedUnlock.builder()
                .id(UUID.randomUUID()).userId(userId).featureKey("ai_suggestions")
                .unlockedAt(OffsetDateTime.now()).expiresAt(OffsetDateTime.now().plusHours(24))
                .build();
        when(unlockRepository.save(any())).thenReturn(persisted);
        RegisterRewardedUnlockCommand cmd = new RegisterRewardedUnlockCommand(userId, "ai_suggestions", 24);

        RewardedUnlock result = useCase.execute(cmd);

        assertThat(result).isSameAs(persisted);
    }
}
