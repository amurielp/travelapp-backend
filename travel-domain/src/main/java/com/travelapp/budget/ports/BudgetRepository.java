package com.travelapp.budget.ports;
import com.travelapp.budget.domain.*;
import java.util.*;

public interface BudgetRepository {
    Budget save(Budget budget);
    Optional<Budget> findByTripId(UUID tripId);
    BudgetItem saveItem(BudgetItem item);
    Optional<BudgetItem> findItemById(UUID id);
    void deleteItem(UUID id);
    List<CategorySummary> getSummaryByTripId(UUID tripId);
}
