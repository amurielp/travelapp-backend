package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExpenseSummaryResponse(
    UUID                   tripId,
    String                 currency,
    BigDecimal             totalEstimated,
    BigDecimal             totalActual,
    BigDecimal             percentageUsed,
    List<ExpenseResponse>  items
) {}
