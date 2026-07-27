package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.BudgetItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.*;

public interface BudgetItemJpaRepository extends JpaRepository<BudgetItemEntity, UUID> {

    List<BudgetItemEntity> findByBudgetId(UUID budgetId);

    @Query("""
        SELECT bi FROM BudgetItemEntity bi
        JOIN BudgetEntity b ON b.id = bi.budgetId
        WHERE b.tripId = :tripId
        ORDER BY bi.scheduledPayAt ASC NULLS LAST, bi.createdAt ASC
    """)
    List<BudgetItemEntity> findByTripIdOrderByScheduledPayAt(@Param("tripId") UUID tripId);

    @Query("""
        SELECT bi FROM BudgetItemEntity bi
        WHERE bi.scheduledPayAt IS NOT NULL
          AND bi.reminderSentAt IS NULL
          AND bi.isPaid = false
          AND bi.scheduledPayAt BETWEEN :from AND :to
    """)
    List<BudgetItemEntity> findDueForReminder(
        @Param("from") OffsetDateTime from,
        @Param("to")   OffsetDateTime to
    );

    @Modifying
    @Query("UPDATE BudgetItemEntity bi SET bi.reminderSentAt = :sentAt WHERE bi.id = :id")
    void markReminderSent(@Param("id") UUID id, @Param("sentAt") OffsetDateTime sentAt);

    @Query("""
        SELECT bi.category,
               SUM(bi.amountEstimated),
               SUM(bi.amountActual),
               COUNT(bi),
               SUM(CASE WHEN bi.isPaid = true THEN 1 ELSE 0 END)
        FROM BudgetItemEntity bi, BudgetEntity b
        WHERE b.id = bi.budgetId AND b.tripId = :tripId
        GROUP BY bi.category
    """)
    List<Object[]> getCategorySummaryRaw(@Param("tripId") UUID tripId);
}
