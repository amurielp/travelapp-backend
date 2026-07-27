package com.travelapp.persistence.repositories;
import com.travelapp.persistence.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ActivityJpaRepository extends JpaRepository<ActivityEntity, UUID> {
    Optional<ActivityEntity> findByEventId(UUID eventId);
    void deleteByEventId(UUID eventId);
}
