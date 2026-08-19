package com.travelapp.users.usecases;

import java.util.UUID;

public record RegisterRewardedUnlockCommand(
    UUID userId,
    String featureKey,
    int ttlHours
) {}
