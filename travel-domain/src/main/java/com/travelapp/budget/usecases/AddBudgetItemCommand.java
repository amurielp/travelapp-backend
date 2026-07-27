package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.BudgetCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AddBudgetItemCommand(
    UUID           tripId,
    UUID           eventId,
    BudgetCategory category,
    String         description,
    BigDecimal     amountEstimated,
    String         currency,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
