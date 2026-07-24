package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BudgetItemResponse(
    UUID           id,
    UUID           budgetId,
    UUID           eventId,
    String         category,
    String         description,
    BigDecimal     amountEstimated,
    BigDecimal     amountActual,
    String         currency,
    boolean        paid,
    OffsetDateTime paidAt,
    String         notes
) {}
