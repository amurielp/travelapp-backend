package com.travelapp.budget.usecases;
import com.travelapp.budget.domain.*;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AddBudgetItemUseCase {
    private final BudgetRepository budgets;

    @Transactional
    public BudgetItem execute(AddBudgetItemCommand cmd) {
        var budget = budgets.findByTripId(cmd.tripId())
            .orElseThrow(() -> new TripNotFoundException(cmd.tripId()));
        var item = BudgetItem.builder()
            .id(UUID.randomUUID())
            .budgetId(budget.getId())
            .eventId(cmd.eventId())
            .category(cmd.category())
            .description(cmd.description())
            .amountEstimated(cmd.amountEstimated())
            .currency(cmd.currency() != null ? cmd.currency() : budget.getCurrency())
            .isPaid(false)
            .notes(cmd.notes())
            .paymentMethodId(cmd.paymentMethodId())
            .scheduledPayAt(cmd.scheduledPayAt())
            .reminderHoursBefore(cmd.reminderHoursBefore())
            .build();
        return budgets.saveItem(item);
    }
}
