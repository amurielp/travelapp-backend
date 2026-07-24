package com.travelapp.budget.domain;
import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Getter @Builder @AllArgsConstructor
public class Budget extends AggregateRoot<UUID> {
    private final UUID    id;
    private final UUID    tripId;
    private String        currency;
    private BigDecimal    totalLimit;
    private List<BudgetItem> items;

    public BigDecimal totalEstimated() {
        return items.stream().map(BudgetItem::getAmountEstimated)
            .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal totalActual() {
        return items.stream().map(BudgetItem::getAmountActual)
            .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public boolean isOverLimit() {
        return totalLimit != null && totalActual().compareTo(totalLimit) > 0;
    }
    public int percentageUsed() {
        if (totalLimit == null || totalLimit.compareTo(BigDecimal.ZERO) == 0) return 0;
        return totalActual().multiply(BigDecimal.valueOf(100)).divide(totalLimit, 0, java.math.RoundingMode.HALF_UP).intValue();
    }
    public void addItem(BudgetItem item) { this.items.add(item); }
}
