package com.travelapp.expenses.port;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(UUID id);

    Optional<Expense> findByEventId(UUID eventId);

    List<Expense> findByTripId(UUID tripId);

    void delete(UUID id);

    List<Expense> findByTripIdOrderByScheduledPayAt(UUID tripId);

    List<Expense> findDueForReminder(OffsetDateTime from, OffsetDateTime to);

    void markReminderSent(UUID id, OffsetDateTime sentAt);

    List<CategorySummary> getSummaryByTripId(UUID tripId);

    record CategorySummary(
        ExpenseCategory category,
        BigDecimal      totalAmount,
        int             numItems,
        int             numPaid
    ) {}
}
