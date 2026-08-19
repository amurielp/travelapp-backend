package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;

public record RewardedUnlockResponse(
    String featureKey,
    OffsetDateTime unlockedAt,
    OffsetDateTime expiresAt
) {}
