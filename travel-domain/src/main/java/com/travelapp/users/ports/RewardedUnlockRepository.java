package com.travelapp.users.ports;

import com.travelapp.users.domain.RewardedUnlock;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface RewardedUnlockRepository {
    RewardedUnlock save(RewardedUnlock unlock);
    List<RewardedUnlock> findActiveByUserId(UUID userId, OffsetDateTime now);
}
