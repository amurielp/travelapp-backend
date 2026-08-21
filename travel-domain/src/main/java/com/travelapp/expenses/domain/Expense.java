package com.travelapp.expenses.domain;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class Expense {
    private final UUID         id;
    private final UUID         tripId;
    private UUID               eventId;
    private ExpenseCategory    category;
    private String             description;
    private BigDecimal         amount;
    private String             currency;
    private boolean            isPaid;
    private OffsetDateTime     paidAt;
    private String             notes;
    private UUID               paymentMethodId;
    private OffsetDateTime     scheduledPayAt;
    private Integer            reminderHoursBefore;
    private OffsetDateTime     reminderSentAt;
    // Derived fields — not persisted, loaded from linked event
    private String             bookingStatus;
    private String             eventTitle;

    public void markPaid() {
        this.isPaid = true;
        this.paidAt = OffsetDateTime.now();
    }

    public void updateAmount(BigDecimal newAmount) {
        this.amount = newAmount;
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
