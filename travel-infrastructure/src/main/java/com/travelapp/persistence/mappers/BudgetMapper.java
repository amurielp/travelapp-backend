package com.travelapp.persistence.mappers;

import com.travelapp.budget.domain.*;
import com.travelapp.persistence.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "items", ignore = true)
    Budget toDomain(BudgetEntity entity);

    BudgetEntity toEntity(Budget budget);

    @Mapping(target = "category", expression = "java(item.getCategory().name())")
    BudgetItemEntity itemToEntity(BudgetItem item);

    @Mapping(target = "category", expression = "java(com.travelapp.budget.domain.BudgetCategory.valueOf(entity.getCategory()))")
    BudgetItem itemToDomain(BudgetItemEntity entity);
}
