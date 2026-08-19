package com.travelapp.subscriptions.usecases;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.ports.SubscriptionRepository;
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
class VerifyReceiptUseCaseTest {

    @Mock SubscriptionRepository repo;
    @InjectMocks VerifyReceiptUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_productIdContainingPremium_planIdIsPremium() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VerifyReceiptCommand cmd = new VerifyReceiptCommand(userId, "apple", "receipt-data", "com.app.premium.monthly");

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        useCase.execute(cmd);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getPlanId()).isEqualTo("premium");
    }

    @Test
    void execute_productIdContainingPro_planIdIsPro() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VerifyReceiptCommand cmd = new VerifyReceiptCommand(userId, "google", "receipt-data", "com.app.pro.annual");

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        useCase.execute(cmd);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getPlanId()).isEqualTo("pro");
    }

    @Test
    void execute_unknownProductId_planIdIsFree() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VerifyReceiptCommand cmd = new VerifyReceiptCommand(userId, "apple", "receipt-data", "com.app.unknown.feature");

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        useCase.execute(cmd);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getPlanId()).isEqualTo("free");
    }

    @Test
    void execute_savedSubscriptionHasActiveStatus() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VerifyReceiptCommand cmd = new VerifyReceiptCommand(userId, "apple", "receipt-data", "com.app.premium.monthly");

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        useCase.execute(cmd);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("active");
    }
}
