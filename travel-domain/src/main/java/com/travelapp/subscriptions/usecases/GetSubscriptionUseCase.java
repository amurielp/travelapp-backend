package com.travelapp.subscriptions.usecases;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.ports.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetSubscriptionUseCase {

    private final SubscriptionRepository repo;

    @Transactional(readOnly = true)
    public Subscription execute(UUID userId) {
        return repo.findActiveByUserId(userId)
                .orElse(Subscription.builder()
                        .userId(userId)
                        .planId("free")
                        .status("active")
                        .autoRenew(false)
                        .build());
    }
}
