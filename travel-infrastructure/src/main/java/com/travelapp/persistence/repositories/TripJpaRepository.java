package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TripEntity;
import com.travelapp.persistence.entities.TripStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TripJpaRepository extends JpaRepository<TripEntity, UUID> {
    List<TripEntity> findByOwnerIdOrderByStartDateDesc(UUID ownerId);
    boolean existsByPublicSlug(String slug);
    List<TripEntity> findByStatusIn(List<TripStatusEntity> statuses);

    // trip_members table not yet in schema — shared trips not implemented
    default List<TripEntity> findSharedWithUser(UUID userId) { return List.of(); }
}
