package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.ExpenseCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AddExpenseCommand(
    UUID            tripId,
    UUID            eventId,
    ExpenseCategory category,
    String          description,
    BigDecimal      amountEstimated,
    String          currency,
    String          notes,
    UUID            paymentMethodId,
    OffsetDateTime  scheduledPayAt,
    Integer         reminderHoursBefore
) {}
