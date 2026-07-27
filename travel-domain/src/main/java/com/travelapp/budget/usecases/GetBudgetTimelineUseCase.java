package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.budget.ports.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetBudgetTimelineUseCase {
    private final BudgetRepository budgets;

    @Transactional(readOnly = true)
    public List<BudgetItem> execute(UUID tripId) {
        return budgets.findItemsByTripIdOrderByScheduledPayAt(tripId);
    }
}
