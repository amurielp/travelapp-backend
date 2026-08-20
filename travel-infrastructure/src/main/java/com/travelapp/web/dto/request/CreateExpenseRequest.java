package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateExpenseRequest(
    UUID           eventId,
    @NotBlank      String         category,
    @NotBlank      String         description,
    @NotNull       BigDecimal     amountEstimated,
    String         currency,
    String         notes,
    UUID           paymentMethodId,
    OffsetDateTime scheduledPayAt,
    Integer        reminderHoursBefore
) {}
