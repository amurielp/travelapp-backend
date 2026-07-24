package com.travelapp.persistence.repositories;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.persistence.entities.BudgetEntity;
import com.travelapp.persistence.mappers.BudgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepository {

    private final BudgetJpaRepository     budgetJpa;
    private final BudgetItemJpaRepository itemJpa;
    private final BudgetMapper            mapper;

    @Override
    public Budget save(Budget budget) {
        BudgetEntity saved = budgetJpa.save(mapper.toEntity(budget));
        return loadWithItems(saved);
    }

    @Override
    public Optional<Budget> findByTripId(UUID tripId) {
        return budgetJpa.findByTripId(tripId).map(this::loadWithItems);
    }

    @Override
    public BudgetItem saveItem(BudgetItem item) {
        return mapper.itemToDomain(itemJpa.save(mapper.itemToEntity(item)));
    }

    @Override
    public Optional<BudgetItem> findItemById(UUID id) {
        return itemJpa.findById(id).map(mapper::itemToDomain);
    }

    @Override
    public void deleteItem(UUID id) { itemJpa.deleteById(id); }

    @Override
    public List<CategorySummary> getSummaryByTripId(UUID tripId) {
        return itemJpa.getCategorySummaryRaw(tripId).stream()
            .map(row -> new CategorySummary(
                BudgetCategory.valueOf((String) row[0]),
                null,
                row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO,
                row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO,
                row[3] != null ? ((Number) row[3]).intValue() : 0,
                row[4] != null ? ((Number) row[4]).intValue() : 0
            )).toList();
    }

    private Budget loadWithItems(BudgetEntity entity) {
        List<BudgetItem> items = itemJpa.findByBudgetId(entity.getId())
            .stream().map(mapper::itemToDomain).toList();
        return Budget.builder()
            .id(entity.getId())
            .tripId(entity.getTripId())
            .currency(entity.getCurrency())
            .totalLimit(entity.getTotalLimit())
            .items(new ArrayList<>(items))
            .build();
    }
}
