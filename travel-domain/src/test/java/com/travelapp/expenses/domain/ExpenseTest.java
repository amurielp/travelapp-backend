package com.travelapp.expenses.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ExpenseTest {

    private Expense expense(boolean paid) {
        return Expense.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .category(ExpenseCategory.ACCOMMODATION).description("Hotel")
            .amountEstimated(new BigDecimal("200")).currency("EUR")
            .isPaid(paid).build();
    }

    @Test
    void markPaid_setsAmountAndPaidFlag() {
        var e = expense(false);
        e.markPaid(new BigDecimal("185"));
        assertThat(e.isPaid()).isTrue();
        assertThat(e.getAmountActual()).isEqualByComparingTo("185");
        assertThat(e.getPaidAt()).isNotNull();
    }

    @Test
    void updateEstimate_changesAmount() {
        var e = expense(false);
        e.updateEstimate(new BigDecimal("250"));
        assertThat(e.getAmountEstimated()).isEqualByComparingTo("250");
    }

    @Test
    void isReminderDue_whenAllConditionsMet_returnsTrue() {
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusHours(2);
        var e = Expense.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .category(ExpenseCategory.FOOD).description("Dinner")
            .amountEstimated(new BigDecimal("50")).currency("EUR").isPaid(false)
            .scheduledPayAt(scheduledAt).reminderHoursBefore(3)
            .build();
        assertThat(e.isReminderDue(OffsetDateTime.now())).isTrue();
    }

    @Test
    void isReminderDue_whenReminderAlreadySent_returnsFalse() {
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusHours(2);
        var e = Expense.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .category(ExpenseCategory.FOOD).description("Dinner")
            .amountEstimated(new BigDecimal("50")).currency("EUR").isPaid(false)
            .scheduledPayAt(scheduledAt).reminderHoursBefore(3)
            .reminderSentAt(OffsetDateTime.now().minusMinutes(5))
            .build();
        assertThat(e.isReminderDue(OffsetDateTime.now())).isFalse();
    }

    @Test
    void isReminderDue_whenNoScheduledPayAt_returnsFalse() {
        var e = expense(false);
        assertThat(e.isReminderDue(OffsetDateTime.now())).isFalse();
    }
}
