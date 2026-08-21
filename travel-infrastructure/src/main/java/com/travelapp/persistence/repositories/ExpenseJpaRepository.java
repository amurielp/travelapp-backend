package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.*;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, UUID> {

    Optional<ExpenseEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<ExpenseEntity> findByEventIdAndDeletedAtIsNull(UUID eventId);

    List<ExpenseEntity> findByTripIdAndDeletedAtIsNull(UUID tripId);

    @Query("""
        SELECT e FROM ExpenseEntity e
        WHERE e.tripId = :tripId AND e.deletedAt IS NULL
        ORDER BY e.scheduledPayAt ASC NULLS LAST, e.createdAt ASC
    """)
    List<ExpenseEntity> findByTripIdOrderByScheduledPayAt(@Param("tripId") UUID tripId);

    @Query("""
        SELECT e FROM ExpenseEntity e
        WHERE e.scheduledPayAt IS NOT NULL
          AND e.reminderSentAt IS NULL
          AND e.isPaid = false
          AND e.deletedAt IS NULL
          AND e.scheduledPayAt BETWEEN :from AND :to
    """)
    List<ExpenseEntity> findDueForReminder(
        @Param("from") OffsetDateTime from,
        @Param("to")   OffsetDateTime to
    );

    @Modifying
    @Query("UPDATE ExpenseEntity e SET e.reminderSentAt = :sentAt WHERE e.id = :id")
    void markReminderSent(@Param("id") UUID id, @Param("sentAt") OffsetDateTime sentAt);

    @Modifying
    @Query("UPDATE ExpenseEntity e SET e.deletedAt = :now WHERE e.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("now") OffsetDateTime now);

    @Query("""
        SELECT e.category,
               SUM(e.amount),
               COUNT(e),
               SUM(CASE WHEN e.isPaid = true THEN 1 ELSE 0 END)
        FROM ExpenseEntity e
        WHERE e.tripId = :tripId AND e.deletedAt IS NULL
        GROUP BY e.category
    """)
    List<Object[]> getCategorySummaryRaw(@Param("tripId") UUID tripId);

    @Query("""
        SELECT e FROM ExpenseEntity e
        WHERE e.tripId IN (SELECT t.id FROM TripEntity t WHERE t.ownerId = :userId)
          AND e.updatedAt > :since AND e.deletedAt IS NULL
    """)
    List<ExpenseEntity> findUpdatedSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);

    @Query("""
        SELECT e.id FROM ExpenseEntity e
        WHERE e.tripId IN (SELECT t.id FROM TripEntity t WHERE t.ownerId = :userId)
          AND e.deletedAt > :since AND e.deletedAt IS NOT NULL
    """)
    List<UUID> findDeletedIdsSince(@Param("userId") UUID userId, @Param("since") OffsetDateTime since);
}
