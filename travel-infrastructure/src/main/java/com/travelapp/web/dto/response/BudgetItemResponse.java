package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BudgetItemResponse(
    UUID           id,
    UUID           budgetId,
    UUID           eventId,
    String         eventTitle,
    String         bookingStatus,
    String         category,
    String         description,
    BigDecimal     amountEstimated,
    BigDecimal     amountActual,
    String         currency,
    boolean        isPaid,
    OffsetDateTime paidAt,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
