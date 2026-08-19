package com.travelapp.subscriptions.usecases;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.ports.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubscriptionUseCaseTest {

    @Mock SubscriptionRepository repo;
    @InjectMocks GetSubscriptionUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_returnsActiveSubscription_whenExists() {
        Subscription active = Subscription.builder()
                .id(UUID.randomUUID()).userId(userId).planId("premium").status("active").build();
        when(repo.findActiveByUserId(userId)).thenReturn(Optional.of(active));

        Subscription result = useCase.execute(userId);

        assertThat(result.getPlanId()).isEqualTo("premium");
        assertThat(result.getStatus()).isEqualTo("active");
    }

    @Test
    void execute_returnsFreePlan_whenNoSubscriptionExists() {
        when(repo.findActiveByUserId(userId)).thenReturn(Optional.empty());

        Subscription result = useCase.execute(userId);

        assertThat(result.getPlanId()).isEqualTo("free");
    }

    @Test
    void execute_defaultSubscriptionHasActiveStatus() {
        when(repo.findActiveByUserId(userId)).thenReturn(Optional.empty());

        Subscription result = useCase.execute(userId);

        assertThat(result.getStatus()).isEqualTo("active");
    }

    @Test
    void execute_defaultSubscriptionBelongsToRequestedUser() {
        when(repo.findActiveByUserId(userId)).thenReturn(Optional.empty());

        Subscription result = useCase.execute(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
    }
}
