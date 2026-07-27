package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.Budget;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class GetBudgetUseCase {
    private final BudgetRepository budgets;
    private final TripRepository   trips;

    @Transactional
    public Budget execute(UUID tripId) {
        return budgets.findByTripId(tripId)
            .orElseGet(() -> {
                // Verifica que el trip existe antes de crear el presupuesto
                trips.findById(tripId)
                    .orElseThrow(() -> new TripNotFoundException(tripId));
                Budget empty = Budget.builder()
                    .id(UUID.randomUUID())
                    .tripId(tripId)
                    .currency("EUR")
                    .items(new ArrayList<>())
                    .build();
                return budgets.save(empty);
            });
    }
}
