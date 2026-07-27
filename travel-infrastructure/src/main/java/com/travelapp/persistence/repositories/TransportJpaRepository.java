package com.travelapp.persistence.repositories;
import com.travelapp.persistence.entities.TransportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TransportJpaRepository extends JpaRepository<TransportEntity, UUID> {
    Optional<TransportEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
