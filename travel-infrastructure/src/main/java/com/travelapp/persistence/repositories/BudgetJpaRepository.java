package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BudgetJpaRepository extends JpaRepository<BudgetEntity, UUID> {
    Optional<BudgetEntity> findByTripId(UUID tripId);
}
