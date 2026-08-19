package com.travelapp.subscriptions.usecases;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.ports.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class VerifyReceiptUseCase {

    private final SubscriptionRepository repo;

    @Transactional
    public Subscription execute(VerifyReceiptCommand cmd) {
        String planId = "free";
        if (cmd.productId() != null) {
            if (cmd.productId().contains("pro")) {
                planId = "pro";
            } else if (cmd.productId().contains("premium")) {
                planId = "premium";
            }
        }

        Subscription sub = Subscription.builder()
                .id(UUID.randomUUID())
                .userId(cmd.userId())
                .planId(planId)
                .status("active")
                .store(cmd.store())
                .autoRenew(true)
                .storeProductId(cmd.productId())
                .storeTransactionId(UUID.randomUUID().toString())
                .startedAt(OffsetDateTime.now())
                .build();

        return repo.save(sub);
    }
}
