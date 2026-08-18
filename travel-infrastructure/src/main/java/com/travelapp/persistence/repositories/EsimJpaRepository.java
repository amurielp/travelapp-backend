package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.EsimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EsimJpaRepository extends JpaRepository<EsimEntity, UUID> {
    Optional<EsimEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
