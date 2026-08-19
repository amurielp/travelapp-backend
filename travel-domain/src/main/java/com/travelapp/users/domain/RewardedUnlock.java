package com.travelapp.users.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class RewardedUnlock {
    private UUID id;
    private UUID userId;
    private String featureKey;
    private OffsetDateTime unlockedAt;
    private OffsetDateTime expiresAt;
}
