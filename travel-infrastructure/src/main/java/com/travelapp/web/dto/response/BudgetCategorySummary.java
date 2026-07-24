package com.travelapp.web.dto.response;

import java.math.BigDecimal;

public record BudgetCategorySummary(
    String     category,
    BigDecimal estimated,
    BigDecimal actual
) {}
