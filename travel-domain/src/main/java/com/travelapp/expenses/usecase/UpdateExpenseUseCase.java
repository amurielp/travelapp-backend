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

        Expense updated = Expense.builder()
            .id(item.getId())
            .tripId(item.getTripId())
            .eventId(item.getEventId())
            .category(item.getCategory())
            .description(cmd.description() != null ? cmd.description() : item.getDescription())
            .amountEstimated(cmd.amountEstimated() != null ? cmd.amountEstimated() : item.getAmountEstimated())
            .amountActual(cmd.amountActual() != null ? cmd.amountActual() : item.getAmountActual())
            .currency(item.getCurrency())
            .isPaid(cmd.isPaid() != null ? cmd.isPaid() : item.isPaid())
            .paidAt(cmd.isPaid() != null && cmd.isPaid() && item.getPaidAt() == null
                ? OffsetDateTime.now() : item.getPaidAt())
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
