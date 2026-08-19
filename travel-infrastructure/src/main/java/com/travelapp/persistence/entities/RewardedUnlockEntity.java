package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rewarded_unlocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardedUnlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "feature_key", nullable = false)
    private String featureKey;

    @Column(name = "unlocked_at")
    private OffsetDateTime unlockedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @PrePersist
    void onCreate() {
        if (unlockedAt == null) {
            unlockedAt = OffsetDateTime.now();
        }
    }
}
