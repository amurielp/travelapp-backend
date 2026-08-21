package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExpenseSummaryResult(
    UUID          tripId,
    String        currency,
    BigDecimal    total,
    BigDecimal    totalPaid,
    List<Expense> items
) {}
