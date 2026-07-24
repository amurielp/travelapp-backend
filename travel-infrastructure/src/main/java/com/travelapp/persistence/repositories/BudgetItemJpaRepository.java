package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.BudgetItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface BudgetItemJpaRepository extends JpaRepository<BudgetItemEntity, UUID> {

    List<BudgetItemEntity> findByBudgetId(UUID budgetId);

    @Query("""
        SELECT bi.category,
               SUM(bi.amountEstimated),
               SUM(bi.amountActual),
               COUNT(bi),
               SUM(CASE WHEN bi.isPaid = true THEN 1 ELSE 0 END)
        FROM BudgetItemEntity bi
        JOIN BudgetEntity b ON b.id = bi.budgetId
        WHERE b.tripId = :tripId
        GROUP BY bi.category
    """)
    List<Object[]> getCategorySummaryRaw(@Param("tripId") UUID tripId);
}
