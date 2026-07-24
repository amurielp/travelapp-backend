package com.travelapp.budget.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BudgetItemTest {

    private BudgetItem item(boolean paid) {
        return BudgetItem.builder()
            .id(UUID.randomUUID()).budgetId(UUID.randomUUID())
            .category(BudgetCategory.ACCOMMODATION).description("Hotel")
            .amountEstimated(new BigDecimal("200")).currency("EUR")
            .isPaid(paid).build();
    }

    @Test
    void markPaid_setsAmountAndPaidFlag() {
        var item = item(false);
        item.markPaid(new BigDecimal("185"));
        assertThat(item.isPaid()).isTrue();
        assertThat(item.getAmountActual()).isEqualByComparingTo("185");
        assertThat(item.getPaidAt()).isNotNull();
    }

    @Test
    void updateEstimate_changesAmount() {
        var item = item(false);
        item.updateEstimate(new BigDecimal("250"));
        assertThat(item.getAmountEstimated()).isEqualByComparingTo("250");
    }
}
