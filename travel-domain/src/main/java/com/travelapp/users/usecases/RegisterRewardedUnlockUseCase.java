package com.travelapp.users.usecases;

import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.ports.RewardedUnlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterRewardedUnlockUseCase {

    private final RewardedUnlockRepository unlockRepository;

    @Transactional
    public RewardedUnlock execute(RegisterRewardedUnlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        RewardedUnlock unlock = RewardedUnlock.builder()
            .id(UUID.randomUUID())
            .userId(cmd.userId())
            .featureKey(cmd.featureKey())
            .unlockedAt(now)
            .expiresAt(now.plusHours(cmd.ttlHours()))
            .build();
        return unlockRepository.save(unlock);
    }
}
