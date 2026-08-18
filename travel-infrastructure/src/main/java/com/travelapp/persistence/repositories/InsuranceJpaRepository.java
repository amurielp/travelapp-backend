package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.InsuranceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InsuranceJpaRepository extends JpaRepository<InsuranceEntity, UUID> {
    Optional<InsuranceEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
