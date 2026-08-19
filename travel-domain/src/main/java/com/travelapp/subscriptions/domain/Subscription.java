package com.travelapp.subscriptions.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Builder
public class Subscription {
    private UUID id;
    private UUID userId;
    private String planId;
    private String status;
    private String store;
    private boolean autoRenew;
    private OffsetDateTime startedAt;
    private OffsetDateTime expiresAt;
    private OffsetDateTime cancelledAt;
    private String storeProductId;
    private String storeTransactionId;
    private LocalDate trialEnd;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
