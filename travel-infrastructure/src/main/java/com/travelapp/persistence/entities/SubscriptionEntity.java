package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(nullable = false)
    private String status;

    private String store;

    @Column(name = "store_product_id")
    private String storeProductId;

    @Column(name = "store_transaction_id")
    private String storeTransactionId;

    @Column(name = "auto_renew")
    private boolean autoRenew;

    @Column(name = "trial_end")
    private LocalDate trialEnd;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
