package com.travelapp.budget.domain;
import java.math.BigDecimal;
public record CategorySummary(BudgetCategory category, BigDecimal limitAmount, BigDecimal totalEstimated, BigDecimal totalActual, int numItems, int numPaid) {
    public int percentageUsed() {
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        return totalActual.multiply(BigDecimal.valueOf(100)).divide(limitAmount, 0, java.math.RoundingMode.HALF_UP).intValue();
    }
}
