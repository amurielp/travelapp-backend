package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.WishlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.*;

public interface WishlistJpaRepository extends JpaRepository<WishlistItemEntity, UUID> {

    Optional<WishlistItemEntity> findByIdAndDeletedAtIsNull(UUID id);

    List<WishlistItemEntity> findByTripIdAndDeletedAtIsNullOrderByPriorityAsc(UUID tripId);

    List<WishlistItemEntity> findByTripIdAndDestinationCityAndDeletedAtIsNullOrderByPriorityAsc(UUID tripId, String destinationCity);

    @Modifying
    @Query("UPDATE WishlistItemEntity w SET w.deletedAt = :now WHERE w.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("now") OffsetDateTime now);

    @Query("SELECT w FROM WishlistItemEntity w JOIN TripEntity t ON t.id = w.tripId WHERE t.ownerId = :userId AND w.updatedAt > :since AND w.deletedAt IS NULL")
    List<WishlistItemEntity> findUpdatedSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);

    @Query("SELECT w.id FROM WishlistItemEntity w JOIN TripEntity t ON t.id = w.tripId WHERE t.ownerId = :userId AND w.deletedAt > :since AND w.deletedAt IS NOT NULL")
    List<UUID> findDeletedIdsSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);
}
