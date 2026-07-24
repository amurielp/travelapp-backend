package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;

public interface PaymentMethodJpaRepository extends JpaRepository<PaymentMethodEntity, UUID> {

    List<PaymentMethodEntity> findByUserIdOrderBySortOrderAscNameAsc(UUID userId);

    List<PaymentMethodEntity> findByUserIdAndIsActiveTrueOrderBySortOrderAscNameAsc(UUID userId);

    @Query(value = """
        SELECT * FROM v_payment_method_report
        WHERE user_id = :userId AND (:tripId IS NULL OR trip_id = :tripId)
        ORDER BY payment_method_name, paid_at DESC
        """, nativeQuery = true)
    List<Object[]> getReportRaw(UUID userId, UUID tripId);
}
