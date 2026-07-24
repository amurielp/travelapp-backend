package com.travelapp.persistence.repositories;

import com.travelapp.budget.domain.*;
import com.travelapp.persistence.entities.*;
import com.travelapp.persistence.mappers.BudgetMapper;
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
class BudgetRepositoryAdapterTest {

    @Mock BudgetJpaRepository budgetJpa;
    @Mock BudgetItemJpaRepository itemJpa;
    @Mock BudgetMapper mapper;
    @InjectMocks BudgetRepositoryAdapter adapter;

    private final UUID budgetId = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();

    private BudgetEntity budgetEntity() {
        return BudgetEntity.builder().id(budgetId).tripId(tripId)
            .currency("EUR").totalLimit(new BigDecimal("500")).build();
    }

    private Budget budget() {
        return Budget.builder().id(budgetId).tripId(tripId)
            .currency("EUR").totalLimit(new BigDecimal("500"))
            .items(new ArrayList<>()).build();
    }

    @Test
    void findByTripId_found_returnsWithItems() {
        var entity = budgetEntity();
        var b = budget();
        when(budgetJpa.findByTripId(tripId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(b);
        when(itemJpa.findByBudgetId(budgetId)).thenReturn(List.of());

        var result = adapter.findByTripId(tripId);

        assertThat(result).isPresent();
        assertThat(result.get().getItems()).isEmpty();
    }

    @Test
    void findByTripId_notFound_returnsEmpty() {
        when(budgetJpa.findByTripId(tripId)).thenReturn(Optional.empty());
        assertThat(adapter.findByTripId(tripId)).isEmpty();
    }

    @Test
    void saveItem_mapsAndSaves() {
        var itemEntity = new BudgetItemEntity();
        var item = BudgetItem.builder().id(UUID.randomUUID()).budgetId(budgetId)
            .category(BudgetCategory.FOOD).description("Lunch")
            .amountEstimated(new BigDecimal("30")).currency("EUR").isPaid(false).build();
        when(mapper.itemToEntity(item)).thenReturn(itemEntity);
        when(itemJpa.save(itemEntity)).thenReturn(itemEntity);
        when(mapper.itemToDomain(itemEntity)).thenReturn(item);

        var result = adapter.saveItem(item);

        assertThat(result).isEqualTo(item);
    }

    @Test
    void deleteItem_callsJpa() {
        var itemId = UUID.randomUUID();
        adapter.deleteItem(itemId);
        verify(itemJpa).deleteById(itemId);
    }

    @Test
    void getSummaryByTripId_mapsRawRows() {
        var row = new Object[]{"FOOD", new BigDecimal("200"), new BigDecimal("150"), 3L, 2L};
        when(itemJpa.getCategorySummaryRaw(tripId)).thenReturn(List.of(row));

        var result = adapter.getSummaryByTripId(tripId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo(BudgetCategory.FOOD);
        assertThat(result.get(0).totalEstimated()).isEqualByComparingTo("200");
        assertThat(result.get(0).numItems()).isEqualTo(3);
        assertThat(result.get(0).numPaid()).isEqualTo(2);
    }
}
