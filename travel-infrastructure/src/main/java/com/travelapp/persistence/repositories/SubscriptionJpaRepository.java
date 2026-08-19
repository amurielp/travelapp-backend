package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.userId = :userId AND s.status IN ('active','trial') ORDER BY s.startedAt DESC")
    Optional<SubscriptionEntity> findActiveByUserId(@Param("userId") UUID userId);
}
