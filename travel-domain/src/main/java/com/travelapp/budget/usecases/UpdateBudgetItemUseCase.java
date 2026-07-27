package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UpdateBudgetItemUseCase {
    private final BudgetRepository budgets;

    @Transactional
    public BudgetItem execute(UpdateBudgetItemCommand cmd) {
        BudgetItem item = budgets.findItemById(cmd.itemId())
            .orElseThrow(() -> new ResourceNotFoundException("BudgetItem", cmd.itemId()));

        BudgetItem updated = BudgetItem.builder()
            .id(item.getId())
            .budgetId(item.getBudgetId())
            .eventId(item.getEventId())
            .category(item.getCategory())
            .description(cmd.description() != null ? cmd.description() : item.getDescription())
            .amountEstimated(cmd.amountEstimated() != null ? cmd.amountEstimated() : item.getAmountEstimated())
            .amountActual(cmd.amountActual() != null ? cmd.amountActual() : item.getAmountActual())
            .currency(item.getCurrency())
            .isPaid(cmd.isPaid() != null ? cmd.isPaid() : item.isPaid())
            .paidAt(cmd.isPaid() != null && cmd.isPaid() && item.getPaidAt() == null
                ? java.time.OffsetDateTime.now() : item.getPaidAt())
            .notes(cmd.notes() != null ? cmd.notes() : item.getNotes())
            .paymentMethodId(cmd.paymentMethodId() != null ? cmd.paymentMethodId() : item.getPaymentMethodId())
            .scheduledPayAt(cmd.scheduledPayAt() != null ? cmd.scheduledPayAt() : item.getScheduledPayAt())
            .reminderHoursBefore(cmd.reminderHoursBefore() != null ? cmd.reminderHoursBefore() : item.getReminderHoursBefore())
            .reminderSentAt(item.getReminderSentAt())
            .eventTitle(item.getEventTitle())
            .bookingStatus(item.getBookingStatus())
            .build();

        return budgets.saveItem(updated);
    }
}
