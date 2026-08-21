package com.travelapp.persistence.repositories;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.persistence.entities.ExpenseEntity;
import com.travelapp.persistence.mappers.ExpenseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseRepositoryAdapterTest {

    @Mock ExpenseJpaRepository expenseJpa;
    @Mock EventJpaRepository   eventJpa;
    @Mock ExpenseMapper        mapper;
    @InjectMocks ExpenseRepositoryAdapter adapter;

    private final UUID tripId    = UUID.randomUUID();
    private final UUID expenseId = UUID.randomUUID();

    private ExpenseEntity entity() {
        ExpenseEntity e = new ExpenseEntity();
        e.setId(expenseId);
        e.setTripId(tripId);
        e.setCategory("FOOD");
        e.setDescription("Lunch");
        e.setAmount(new BigDecimal("30"));
        e.setCurrency("EUR");
        return e;
    }

    private Expense domain() {
        return Expense.builder().id(expenseId).tripId(tripId)
            .category(ExpenseCategory.FOOD).description("Lunch")
            .amount(new BigDecimal("30")).currency("EUR").isPaid(false).build();
    }

    @Test
    void findByTripId_returnsEnrichedList() {
        var e = entity();
        var d = domain();
        when(expenseJpa.findByTripIdAndDeletedAtIsNull(tripId)).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(d);

        var result = adapter.findByTripId(tripId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTripId()).isEqualTo(tripId);
    }

    @Test
    void findByTripId_empty_returnsEmptyList() {
        when(expenseJpa.findByTripIdAndDeletedAtIsNull(tripId)).thenReturn(List.of());
        assertThat(adapter.findByTripId(tripId)).isEmpty();
    }

    @Test
    void save_mapsAndSaves() {
        var d = domain();
        var e = entity();
        when(mapper.toEntity(d)).thenReturn(e);
        when(expenseJpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(d);

        var result = adapter.save(d);

        assertThat(result).isEqualTo(d);
        verify(expenseJpa).save(e);
    }

    @Test
    void getSummaryByTripId_mapsRawRows() {
        var row = new Object[]{"FOOD", new BigDecimal("200"), 3L, 2L};
        when(expenseJpa.getCategorySummaryRaw(tripId)).thenReturn(List.<Object[]>of(row));

        var result = adapter.getSummaryByTripId(tripId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo(ExpenseCategory.FOOD);
        assertThat(result.get(0).totalAmount()).isEqualByComparingTo("200");
        assertThat(result.get(0).numItems()).isEqualTo(3);
        assertThat(result.get(0).numPaid()).isEqualTo(2);
    }
}
