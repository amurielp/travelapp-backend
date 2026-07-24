package com.travelapp.budget.domain;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class BudgetItem {
    private final UUID       id;
    private final UUID       budgetId;
    private UUID             eventId;
    private BudgetCategory   category;
    private String           description;
    private BigDecimal       amountEstimated;
    private BigDecimal       amountActual;
    private String           currency;
    private boolean          isPaid;
    private OffsetDateTime   paidAt;
    private String           notes;

    public void markPaid(BigDecimal actual) {
        this.amountActual = actual;
        this.isPaid       = true;
        this.paidAt       = OffsetDateTime.now();
    }
    public void updateEstimate(BigDecimal amount) { this.amountEstimated = amount; }
}
