package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetExpenseSummaryUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public ExpenseSummaryResult execute(UUID tripId) {
        List<Expense> items = expenseRepository.findByTripId(tripId);

        BigDecimal totalEstimated = items.stream()
            .map(Expense::getAmountEstimated)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActual = items.stream()
            .map(Expense::getAmountActual)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentageUsed = totalEstimated.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : totalActual.multiply(BigDecimal.valueOf(100))
                .divide(totalEstimated, 2, RoundingMode.HALF_UP);

        String currency = items.isEmpty() ? "EUR" : items.get(0).getCurrency();

        return new ExpenseSummaryResult(tripId, currency, totalEstimated, totalActual, percentageUsed, items);
    }
}
