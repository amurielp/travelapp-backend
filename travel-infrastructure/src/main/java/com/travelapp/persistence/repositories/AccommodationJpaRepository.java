package com.travelapp.persistence.repositories;
import com.travelapp.persistence.entities.AccommodationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AccommodationJpaRepository extends JpaRepository<AccommodationEntity, UUID> {
    Optional<AccommodationEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
