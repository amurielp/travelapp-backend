package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TripEntity;
import com.travelapp.persistence.entities.TripStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.*;

public interface TripJpaRepository extends JpaRepository<TripEntity, UUID> {

    Optional<TripEntity> findByIdAndDeletedAtIsNull(UUID id);

    @Query("SELECT t FROM TripEntity t WHERE t.ownerId = :ownerId AND t.deletedAt IS NULL ORDER BY t.startDate DESC")
    List<TripEntity> findByOwnerIdOrderByStartDateDesc(@Param("ownerId") UUID ownerId);

    boolean existsByPublicSlug(String slug);

    @Query("SELECT t FROM TripEntity t WHERE t.status IN :statuses AND t.deletedAt IS NULL")
    List<TripEntity> findByStatusIn(@Param("statuses") List<TripStatusEntity> statuses);

    @Modifying
    @Query("UPDATE TripEntity t SET t.deletedAt = :now WHERE t.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("now") OffsetDateTime now);

    // trip_members table not yet in schema — shared trips not implemented
    default List<TripEntity> findSharedWithUser(UUID userId) { return List.of(); }

    @Query("SELECT t FROM TripEntity t WHERE t.ownerId = :userId AND t.updatedAt > :since AND t.deletedAt IS NULL")
    List<TripEntity> findUpdatedSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);

    @Query("SELECT t.id FROM TripEntity t WHERE t.ownerId = :userId AND t.deletedAt > :since AND t.deletedAt IS NOT NULL")
    List<UUID> findDeletedIdsSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);
}
