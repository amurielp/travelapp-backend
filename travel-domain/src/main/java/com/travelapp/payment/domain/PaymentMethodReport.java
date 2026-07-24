package com.travelapp.payment.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Resumen agregado de gastos por medio de pago */
public record PaymentMethodReport(
    UUID              paymentMethodId,
    String            paymentMethodName,
    PaymentMethodType type,
    String            currency,
    BigDecimal        totalConfirmed,
    BigDecimal        totalReserved,
    BigDecimal        totalPending,
    BigDecimal        totalAll,
    int               numExpenses,
    List<PaymentMethodReportLine> lines
) {}

