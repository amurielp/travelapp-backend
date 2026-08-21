package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetExpenseSummaryUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public ExpenseSummaryResult execute(UUID tripId) {
        List<Expense> items = expenseRepository.findByTripId(tripId);

        BigDecimal total = items.stream()
            .map(Expense::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = items.stream()
            .filter(Expense::isPaid)
            .map(Expense::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = items.isEmpty() ? "EUR" : items.get(0).getCurrency();

        return new ExpenseSummaryResult(tripId, currency, total, totalPaid, items);
    }
}
