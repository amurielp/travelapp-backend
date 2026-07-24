package com.travelapp.budget.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BudgetTest {

    private BudgetItem item(BigDecimal estimated, BigDecimal actual, boolean paid) {
        return BudgetItem.builder()
            .id(UUID.randomUUID()).budgetId(UUID.randomUUID())
            .category(BudgetCategory.FOOD).description("item")
            .amountEstimated(estimated).amountActual(actual)
            .currency("EUR").isPaid(paid).build();
    }

    private Budget budget(BigDecimal limit, List<BudgetItem> items) {
        return Budget.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .currency("EUR").totalLimit(limit)
            .items(new ArrayList<>(items)).build();
    }

    @Test
    void totalEstimated_sumsItems() {
        var b = budget(null, List.of(
            item(new BigDecimal("100"), null, false),
            item(new BigDecimal("50"), null, false)
        ));
        assertThat(b.totalEstimated()).isEqualByComparingTo("150");
    }

    @Test
    void totalActual_sumsItems() {
        var b = budget(null, List.of(
            item(null, new BigDecimal("80"), true),
            item(null, new BigDecimal("40"), true)
        ));
        assertThat(b.totalActual()).isEqualByComparingTo("120");
    }

    @Test
    void totalActual_ignoresNullActual() {
        var b = budget(null, List.of(
            item(new BigDecimal("100"), null, false),
            item(null, new BigDecimal("30"), true)
        ));
        assertThat(b.totalActual()).isEqualByComparingTo("30");
    }

    @Test
    void isOverLimit_whenActualExceedsLimit_returnsTrue() {
        var b = budget(new BigDecimal("100"), List.of(
            item(null, new BigDecimal("120"), true)
        ));
        assertThat(b.isOverLimit()).isTrue();
    }

    @Test
    void isOverLimit_whenActualBelowLimit_returnsFalse() {
        var b = budget(new BigDecimal("200"), List.of(
            item(null, new BigDecimal("100"), true)
        ));
        assertThat(b.isOverLimit()).isFalse();
    }

    @Test
    void isOverLimit_whenNoLimit_returnsFalse() {
        var b = budget(null, List.of(item(null, new BigDecimal("999"), true)));
        assertThat(b.isOverLimit()).isFalse();
    }

    @Test
    void percentageUsed_calculatesCorrectly() {
        var b = budget(new BigDecimal("200"), List.of(
            item(null, new BigDecimal("100"), true)
        ));
        assertThat(b.percentageUsed()).isEqualTo(50);
    }

    @Test
    void percentageUsed_whenNoLimit_returnsZero() {
        var b = budget(null, List.of(item(null, new BigDecimal("100"), true)));
        assertThat(b.percentageUsed()).isEqualTo(0);
    }

    @Test
    void addItem_appendsToList() {
        var b = budget(null, new ArrayList<>());
        var newItem = item(new BigDecimal("50"), null, false);
        b.addItem(newItem);
        assertThat(b.getItems()).hasSize(1).contains(newItem);
    }
}
