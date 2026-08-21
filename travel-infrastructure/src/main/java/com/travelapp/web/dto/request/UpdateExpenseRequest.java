package com.travelapp.web.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateExpenseRequest(
    String         description,
    BigDecimal     amount,
    Boolean        isPaid,
    OffsetDateTime paidAt,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
