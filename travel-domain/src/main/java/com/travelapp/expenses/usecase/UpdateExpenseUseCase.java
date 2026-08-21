package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service @RequiredArgsConstructor
public class UpdateExpenseUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public Expense execute(UpdateExpenseCommand cmd) {
        Expense item = expenseRepository.findById(cmd.itemId())
            .orElseThrow(() -> new ResourceNotFoundException("Expense", cmd.itemId()));

        boolean nowPaid = cmd.isPaid() != null ? cmd.isPaid() : item.isPaid();
        OffsetDateTime paidAt = nowPaid
            ? (cmd.paidAt() != null ? cmd.paidAt()
                : item.getPaidAt() != null ? item.getPaidAt()
                : OffsetDateTime.now())
            : null;

        Expense updated = Expense.builder()
            .id(item.getId())
            .tripId(item.getTripId())
            .eventId(item.getEventId())
            .category(item.getCategory())
            .description(cmd.description() != null ? cmd.description() : item.getDescription())
            .amount(cmd.amount() != null ? cmd.amount() : item.getAmount())
            .currency(item.getCurrency())
            .isPaid(nowPaid)
            .paidAt(paidAt)
            .notes(cmd.notes() != null ? cmd.notes() : item.getNotes())
            .paymentMethodId(cmd.paymentMethodId() != null ? cmd.paymentMethodId() : item.getPaymentMethodId())
            .scheduledPayAt(cmd.scheduledPayAt() != null ? cmd.scheduledPayAt() : item.getScheduledPayAt())
            .reminderHoursBefore(cmd.reminderHoursBefore() != null ? cmd.reminderHoursBefore() : item.getReminderHoursBefore())
            .reminderSentAt(item.getReminderSentAt())
            .eventTitle(item.getEventTitle())
            .bookingStatus(item.getBookingStatus())
            .build();

        return expenseRepository.save(updated);
    }
}
