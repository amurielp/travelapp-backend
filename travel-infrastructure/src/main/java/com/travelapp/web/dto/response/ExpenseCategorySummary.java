package com.travelapp.web.dto.response;

import java.math.BigDecimal;

public record ExpenseCategorySummary(
    String     category,
    BigDecimal total,
    int        numItems,
    int        numPaid
) {}
