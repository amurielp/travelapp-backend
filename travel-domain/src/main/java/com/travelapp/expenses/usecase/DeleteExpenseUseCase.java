package com.travelapp.expenses.usecase;

import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class DeleteExpenseUseCase {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public void execute(UUID itemId) {
        if (expenseRepository.findById(itemId).isEmpty())
            throw new ResourceNotFoundException("Expense", itemId);
        expenseRepository.delete(itemId);
    }
}
