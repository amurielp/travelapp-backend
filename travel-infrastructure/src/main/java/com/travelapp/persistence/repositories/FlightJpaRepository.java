package com.travelapp.persistence.repositories;
import com.travelapp.persistence.entities.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FlightJpaRepository extends JpaRepository<FlightEntity, UUID> {
    Optional<FlightEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
