package com.travelapp.subscriptions.ports;

import com.travelapp.subscriptions.domain.Subscription;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {
    Subscription save(Subscription sub);
    Optional<Subscription> findActiveByUserId(UUID userId);
}
