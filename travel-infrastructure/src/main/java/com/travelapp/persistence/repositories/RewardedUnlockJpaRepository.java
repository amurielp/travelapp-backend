package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.RewardedUnlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface RewardedUnlockJpaRepository extends JpaRepository<RewardedUnlockEntity, UUID> {

    @Query("SELECT r FROM RewardedUnlockEntity r WHERE r.userId = :userId AND r.expiresAt > :now")
    List<RewardedUnlockEntity> findActiveByUserId(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);
}
