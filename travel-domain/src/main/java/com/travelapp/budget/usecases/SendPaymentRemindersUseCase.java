package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.budget.ports.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service @RequiredArgsConstructor
public class SendPaymentRemindersUseCase {
    private final BudgetRepository budgets;

    @Transactional
    public int execute() {
        OffsetDateTime now = OffsetDateTime.now();
        // window: items whose scheduledPayAt falls in the next reminderHoursBefore hours
        // We query a broad 72-hour window and filter by reminderHoursBefore in domain
        OffsetDateTime to = now.plusHours(72);
        List<BudgetItem> due = budgets.findItemsDueForReminder(now, to).stream()
            .filter(item -> item.isReminderDue(now))
            .toList();

        for (BudgetItem item : due) {
            log.info("Payment reminder due: budgetItem={} scheduled={} description={}",
                item.getId(), item.getScheduledPayAt(), item.getDescription());
            budgets.markReminderSent(item.getId(), now);
        }
        return due.size();
    }
}
