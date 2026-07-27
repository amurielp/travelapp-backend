package com.travelapp.persistence.mappers;

import com.travelapp.budget.domain.*;
import com.travelapp.persistence.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "items",     ignore = true)
    Budget toDomain(BudgetEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    BudgetEntity toEntity(Budget budget);

    @Mapping(target = "category",  expression = "java(item.getCategory().name())")
    @Mapping(target = "isPaid",    source = "paid")
    @Mapping(target = "createdAt", ignore = true)
    BudgetItemEntity itemToEntity(BudgetItem item);

    @Mapping(target = "category",      expression = "java(com.travelapp.budget.domain.BudgetCategory.valueOf(entity.getCategory()))")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "bookingStatus", ignore = true)
    @Mapping(target = "eventTitle",    ignore = true)
    BudgetItem itemToDomain(BudgetItemEntity entity);
}
