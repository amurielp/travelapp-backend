package com.travelapp.budget.usecases;
import com.travelapp.budget.domain.Budget;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class GetBudgetUseCase {
    private final BudgetRepository budgets;
    @Transactional(readOnly = true)
    public Budget execute(UUID tripId) {
        return budgets.findByTripId(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));
    }
}
