package com.travelapp.expenses.usecase;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.expenses.port.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddExpenseUseCaseTest {

    @Mock ExpenseRepository expenseRepository;
    @InjectMocks AddExpenseUseCase useCase;

    private final UUID tripId = UUID.randomUUID();

    @Test
    void execute_savesExpenseWithDefaultCurrencyWhenNull() {
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddExpenseCommand(tripId, null, ExpenseCategory.FOOD,
            "Dinner", new BigDecimal("45"), null, null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.isPaid()).isFalse();
        assertThat(result.getTripId()).isEqualTo(tripId);
        verify(expenseRepository).save(any());
    }

    @Test
    void execute_usesCommandCurrencyWhenProvided() {
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddExpenseCommand(tripId, null, ExpenseCategory.TRANSPORT,
            "Taxi", new BigDecimal("20"), "USD", null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void execute_setsCorrectCategory() {
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddExpenseCommand(tripId, null, ExpenseCategory.ACTIVITIES,
            "Museum", new BigDecimal("15"), "EUR", null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getCategory()).isEqualTo(ExpenseCategory.ACTIVITIES);
    }

    @Test
    void execute_generatesNewId() {
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new AddExpenseCommand(tripId, null, ExpenseCategory.FOOD,
            "Lunch", new BigDecimal("30"), "EUR", null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getId()).isNotNull();
    }
}
