package com.travelapp.persistence.repositories;

import com.travelapp.persistence.mappers.RewardedUnlockMapper;
import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.ports.RewardedUnlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RewardedUnlockRepositoryAdapter implements RewardedUnlockRepository {

    private final RewardedUnlockJpaRepository jpa;
    private final RewardedUnlockMapper mapper;

    @Override
    public RewardedUnlock save(RewardedUnlock unlock) {
        return mapper.toDomain(jpa.save(mapper.toEntity(unlock)));
    }

    @Override
    public List<RewardedUnlock> findActiveByUserId(UUID userId, OffsetDateTime now) {
        return jpa.findActiveByUserId(userId, now).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}
