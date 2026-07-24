package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentMethodReportResponse(
    UUID       paymentMethodId,
    String     paymentMethodName,
    String     type,
    String     currency,
    BigDecimal totalConfirmed,
    BigDecimal totalReserved,
    BigDecimal totalPending,
    BigDecimal totalAll,
    int        numExpenses
) {}
