package com.travelapp.payment.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentMethodReportLine(
    String         expenseType,
    String         description,
    BigDecimal     amount,
    String         currency,
    String         purchaseStatus,
    OffsetDateTime paidAt
) {}
