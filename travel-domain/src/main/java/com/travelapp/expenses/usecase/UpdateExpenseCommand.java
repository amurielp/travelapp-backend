package com.travelapp.expenses.usecase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateExpenseCommand(
    UUID           itemId,
    UUID           tripId,
    String         description,
    BigDecimal     amountEstimated,
    BigDecimal     amountActual,
    Boolean        isPaid,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
