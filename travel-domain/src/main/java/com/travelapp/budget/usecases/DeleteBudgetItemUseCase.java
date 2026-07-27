package com.travelapp.budget.usecases;

import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class DeleteBudgetItemUseCase {
    private final BudgetRepository budgets;

    @Transactional
    public void execute(UUID itemId) {
        if (budgets.findItemById(itemId).isEmpty())
            throw new ResourceNotFoundException("BudgetItem", itemId);
        budgets.deleteItem(itemId);
    }
}
