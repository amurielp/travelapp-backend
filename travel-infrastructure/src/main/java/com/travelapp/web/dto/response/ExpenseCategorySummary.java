package com.travelapp.web.dto.response;

import java.math.BigDecimal;

public record ExpenseCategorySummary(
    String     category,
    BigDecimal totalEstimated,
    BigDecimal totalActual,
    int        numItems,
    int        numPaid,
    BigDecimal percentageUsed
) {}
