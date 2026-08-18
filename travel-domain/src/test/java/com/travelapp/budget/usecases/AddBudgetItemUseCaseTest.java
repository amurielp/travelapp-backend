package com.travelapp.budget.usecases;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddBudgetItemUseCaseTest {

    @Mock BudgetRepository budgets;
    @InjectMocks AddBudgetItemUseCase useCase;

    private final UUID tripId = UUID.randomUUID();

    private Budget existingBudget() {
        return Budget.builder()
            .id(UUID.randomUUID()).tripId(tripId)
            .currency("EUR").totalLimit(new BigDecimal("1000"))
            .items(new ArrayList<>()).build();
    }

    @Test
    void execute_savesItemWithBudgetCurrencyWhenNull() {
        var budget = existingBudget();
        when(budgets.findByTripId(tripId)).thenReturn(Optional.of(budget));
        when(budgets.saveItem(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddBudgetItemCommand(tripId, null, BudgetCategory.FOOD,
            "Dinner", new BigDecimal("45"), null, null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.isPaid()).isFalse();
        assertThat(result.getBudgetId()).isEqualTo(budget.getId());
    }

    @Test
    void execute_usesCommandCurrencyWhenProvided() {
        var budget = existingBudget();
        when(budgets.findByTripId(tripId)).thenReturn(Optional.of(budget));
        when(budgets.saveItem(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddBudgetItemCommand(tripId, null, BudgetCategory.TRANSPORT,
            "Taxi", new BigDecimal("20"), "USD", null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void execute_budgetNotFound_throwsTripNotFoundException() {
        when(budgets.findByTripId(tripId)).thenReturn(Optional.empty());

        var cmd = new AddBudgetItemCommand(tripId, null, BudgetCategory.FOOD,
            "Lunch", new BigDecimal("30"), null, null, null, null, null);

        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(TripNotFoundException.class);
        verify(budgets, never()).saveItem(any());
    }
}
