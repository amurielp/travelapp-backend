package com.travelapp.web.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBudgetItemRequest(
    UUID       eventId,
    String     category,
    String     description,
    BigDecimal amountEstimated,
    String     currency
) {}
