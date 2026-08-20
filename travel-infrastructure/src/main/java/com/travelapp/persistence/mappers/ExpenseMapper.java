package com.travelapp.persistence.mappers;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.persistence.entities.ExpenseEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "category",      expression = "java(expense.getCategory().name())")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "updatedAt",     ignore = true)
    @Mapping(target = "deletedAt",     ignore = true)
    ExpenseEntity toEntity(Expense expense);

    @Mapping(target = "category",      expression = "java(com.travelapp.expenses.domain.ExpenseCategory.valueOf(entity.getCategory()))")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "bookingStatus", ignore = true)
    @Mapping(target = "eventTitle",    ignore = true)
    Expense toDomain(ExpenseEntity entity);
}
