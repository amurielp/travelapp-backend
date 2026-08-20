package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetExpenseTimelineUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public List<Expense> execute(UUID tripId) {
        return expenseRepository.findByTripIdOrderByScheduledPayAt(tripId);
    }
}
