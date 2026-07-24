package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TripGapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

public interface TripGapJpaRepository extends JpaRepository<TripGapEntity, UUID> {
    List<TripGapEntity> findByTripIdAndStatusOrderByAffectedFromAsc(UUID tripId, String status);
    List<TripGapEntity> findByTripIdOrderByAffectedFromAsc(UUID tripId);
    @Transactional
    void deleteByTripId(UUID tripId);
}
