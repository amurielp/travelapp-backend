package com.travelapp.web.mappers;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.usecases.AddBudgetItemCommand;
import com.travelapp.web.dto.request.CreateBudgetItemRequest;
import com.travelapp.web.dto.response.BudgetItemResponse;
import com.travelapp.web.dto.response.BudgetResponse;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetDtoMapper {

    @Mapping(target = "totalEstimated", expression = "java(budget.totalEstimated())")
    @Mapping(target = "totalActual",    expression = "java(budget.totalActual())")
    @Mapping(target = "percentageUsed", expression = "java(budget.percentageUsed())")
    BudgetResponse toResponse(Budget budget);

    @Mapping(target = "category", expression = "java(item.getCategory() != null ? item.getCategory().name() : null)")
    @Mapping(target = "paid",     source = "paid")
    BudgetItemResponse toItemResponse(BudgetItem item);

    @Mapping(target = "tripId",          source = "tripId")
    @Mapping(target = "eventId",         source = "req.eventId")
    @Mapping(target = "description",     source = "req.description")
    @Mapping(target = "amountEstimated", source = "req.amountEstimated")
    @Mapping(target = "currency",        source = "req.currency")
    @Mapping(target = "category",        expression = "java(BudgetCategory.valueOf(req.category().toUpperCase()))")
    AddBudgetItemCommand toCommand(CreateBudgetItemRequest req, UUID tripId);
}
