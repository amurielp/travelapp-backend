package com.travelapp.web.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateBudgetItemRequest(
    String         description,
    BigDecimal     amountEstimated,
    BigDecimal     amountActual,
    Boolean        isPaid,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
