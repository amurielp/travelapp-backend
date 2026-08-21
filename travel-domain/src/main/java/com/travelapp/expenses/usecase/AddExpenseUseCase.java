package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AddExpenseUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public Expense execute(AddExpenseCommand cmd) {
        boolean paid = Boolean.TRUE.equals(cmd.isPaid());
        var expense = Expense.builder()
            .id(UUID.randomUUID())
            .tripId(cmd.tripId())
            .eventId(cmd.eventId())
            .category(cmd.category())
            .description(cmd.description())
            .amount(cmd.amount())
            .currency(cmd.currency() != null ? cmd.currency() : "EUR")
            .isPaid(paid)
            .paidAt(paid ? (cmd.paidAt() != null ? cmd.paidAt() : OffsetDateTime.now()) : null)
            .notes(cmd.notes())
            .paymentMethodId(cmd.paymentMethodId())
            .scheduledPayAt(cmd.scheduledPayAt())
            .reminderHoursBefore(cmd.reminderHoursBefore())
            .build();
        return expenseRepository.save(expense);
    }
}
