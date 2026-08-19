package com.travelapp.users.usecases;

import com.travelapp.shared.exceptions.UserNotFoundException;
import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.domain.User;
import com.travelapp.users.domain.UserFeatures;
import com.travelapp.users.domain.UserPlan;
import com.travelapp.users.ports.RewardedUnlockRepository;
import com.travelapp.users.ports.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserFeaturesUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock RewardedUnlockRepository unlockRepository;
    @InjectMocks GetUserFeaturesUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    private User userWithPlan(UserPlan plan) {
        return User.builder().id(userId).keycloakId("kc-1").plan(plan).build();
    }

    @Test
    void execute_freePlan_showAdsTrue_aiSuggestionsFalse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPlan(UserPlan.FREE)));
        when(unlockRepository.findActiveByUserId(eq(userId), any())).thenReturn(Collections.emptyList());

        UserFeatures result = useCase.execute(userId);

        assertThat(result.isShowAds()).isTrue();
        assertThat(result.isAiSuggestions()).isFalse();
    }

    @Test
    void execute_premiumPlan_showAdsFalse_aiSuggestionsTrue() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPlan(UserPlan.PREMIUM)));
        when(unlockRepository.findActiveByUserId(eq(userId), any())).thenReturn(Collections.emptyList());

        UserFeatures result = useCase.execute(userId);

        assertThat(result.isShowAds()).isFalse();
        assertThat(result.isAiSuggestions()).isTrue();
    }

    @Test
    void execute_rewardedAiSuggestionsUnlock_freePlan_aiSuggestionsTrue() {
        RewardedUnlock unlock = RewardedUnlock.builder()
                .id(UUID.randomUUID()).userId(userId).featureKey("ai_suggestions")
                .unlockedAt(OffsetDateTime.now()).expiresAt(OffsetDateTime.now().plusHours(24))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPlan(UserPlan.FREE)));
        when(unlockRepository.findActiveByUserId(eq(userId), any())).thenReturn(List.of(unlock));

        UserFeatures result = useCase.execute(userId);

        assertThat(result.isAiSuggestions()).isTrue();
        assertThat(result.isShowAds()).isTrue();
    }

    @Test
    void execute_noActiveUnlocks_rewardedUnlocksListIsEmpty() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithPlan(UserPlan.FREE)));
        when(unlockRepository.findActiveByUserId(eq(userId), any())).thenReturn(Collections.emptyList());

        UserFeatures result = useCase.execute(userId);

        assertThat(result.getRewardedUnlocks()).isEmpty();
    }

    @Test
    void execute_unknownUser_throwsUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
