package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BudgetResponse(
    UUID                  id,
    UUID                  tripId,
    String                currency,
    BigDecimal            totalLimit,
    BigDecimal            totalEstimated,
    BigDecimal            totalActual,
    int                   percentageUsed,
    List<BudgetItemResponse> items
) {}
