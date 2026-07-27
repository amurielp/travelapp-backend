package com.travelapp.web.dto.response;

import java.math.BigDecimal;

public record BudgetCategorySummary(
    String     category,
    BigDecimal limitAmount,
    BigDecimal totalEstimated,
    BigDecimal totalActual,
    int        numItems,
    int        numPaid,
    int        percentageUsed
) {}
