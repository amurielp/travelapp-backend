package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.CategorySummary;
import com.travelapp.budget.ports.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetBudgetSummaryUseCase {
    private final BudgetRepository budgets;

    @Transactional(readOnly = true)
    public List<CategorySummary> execute(UUID tripId) {
        return budgets.getSummaryByTripId(tripId);
    }
}
