package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service @RequiredArgsConstructor
public class SendPaymentRemindersUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public int execute() {
        OffsetDateTime now = OffsetDateTime.now();
        // Query a broad 72-hour window and filter by reminderHoursBefore in domain
        OffsetDateTime to = now.plusHours(72);
        List<Expense> due = expenseRepository.findDueForReminder(now, to).stream()
            .filter(expense -> expense.isReminderDue(now))
            .toList();

        for (Expense expense : due) {
            log.info("Payment reminder due: expense={} scheduled={} description={}",
                expense.getId(), expense.getScheduledPayAt(), expense.getDescription());
            expenseRepository.markReminderSent(expense.getId(), now);
        }
        return due.size();
    }
}
