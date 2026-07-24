package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.*;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {

    List<EventEntity> findByTripIdOrderByStartDatetimeAsc(UUID tripId);

    @Query("""
        SELECT e FROM EventEntity e
        WHERE e.tripId = :tripId
          AND e.startDatetime >= :from
          AND e.startDatetime < :to
        ORDER BY e.startDatetime
    """)
    List<EventEntity> findByTripIdAndDateRange(
        @Param("tripId") UUID tripId,
        @Param("from")   OffsetDateTime from,
        @Param("to")     OffsetDateTime to
    );
}
