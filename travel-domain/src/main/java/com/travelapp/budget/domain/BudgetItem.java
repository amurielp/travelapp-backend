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
    private UUID             paymentMethodId;
    private OffsetDateTime   scheduledPayAt;
    private Integer          reminderHoursBefore;
    private OffsetDateTime   reminderSentAt;
    // Campos derivados — no persisten, se cargan desde el evento vinculado
    private String           bookingStatus;
    private String           eventTitle;

    public void markPaid(BigDecimal actual) {
        this.amountActual = actual;
        this.isPaid       = true;
        this.paidAt       = OffsetDateTime.now();
    }

    public void schedulePayment(OffsetDateTime payAt, Integer hoursBeforeReminder) {
        this.scheduledPayAt      = payAt;
        this.reminderHoursBefore = hoursBeforeReminder;
    }

    public void markReminderSent() {
        this.reminderSentAt = OffsetDateTime.now();
    }

    public boolean isReminderDue(OffsetDateTime now) {
        if (scheduledPayAt == null || reminderHoursBefore == null || reminderSentAt != null) return false;
        return !scheduledPayAt.isBefore(now)
            && scheduledPayAt.isBefore(now.plusHours(reminderHoursBefore));
    }
}
