package com.travelapp.web.mappers;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.usecases.AddBudgetItemCommand;
import com.travelapp.budget.usecases.UpdateBudgetItemCommand;
import com.travelapp.web.dto.request.CreateBudgetItemRequest;
import com.travelapp.web.dto.request.UpdateBudgetItemRequest;
import com.travelapp.web.dto.response.*;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetDtoMapper {

    @Mapping(target = "totalEstimated", expression = "java(budget.totalEstimated())")
    @Mapping(target = "totalActual",    expression = "java(budget.totalActual())")
    @Mapping(target = "percentageUsed", expression = "java(budget.percentageUsed())")
    BudgetResponse toResponse(Budget budget);

    @Mapping(target = "category",      expression = "java(item.getCategory() != null ? item.getCategory().name() : null)")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "eventTitle",    source = "eventTitle")
    @Mapping(target = "bookingStatus", source = "bookingStatus")
    BudgetItemResponse toItemResponse(BudgetItem item);

    @Mapping(target = "category",          expression = "java(cs.category().name())")
    @Mapping(target = "limitAmount",       source = "limitAmount")
    @Mapping(target = "totalEstimated",    source = "totalEstimated")
    @Mapping(target = "totalActual",       source = "totalActual")
    @Mapping(target = "numItems",          source = "numItems")
    @Mapping(target = "numPaid",           source = "numPaid")
    @Mapping(target = "percentageUsed",    expression = "java(cs.percentageUsed())")
    BudgetCategorySummary toCategorySummary(CategorySummary cs);

    @Mapping(target = "tripId",              source = "tripId")
    @Mapping(target = "eventId",             source = "req.eventId")
    @Mapping(target = "description",         source = "req.description")
    @Mapping(target = "amountEstimated",     source = "req.amountEstimated")
    @Mapping(target = "currency",            source = "req.currency")
    @Mapping(target = "category",            expression = "java(BudgetCategory.valueOf(req.category().toUpperCase()))")
    @Mapping(target = "notes",               source = "req.notes")
    @Mapping(target = "paymentMethodId",     source = "req.paymentMethodId")
    @Mapping(target = "scheduledPayAt",      source = "req.scheduledPayAt")
    @Mapping(target = "reminderHoursBefore", source = "req.reminderHoursBefore")
    AddBudgetItemCommand toAddCommand(CreateBudgetItemRequest req, UUID tripId);

    @Mapping(target = "itemId",              source = "itemId")
    @Mapping(target = "tripId",              source = "tripId")
    @Mapping(target = "description",         source = "req.description")
    @Mapping(target = "amountEstimated",     source = "req.amountEstimated")
    @Mapping(target = "amountActual",        source = "req.amountActual")
    @Mapping(target = "isPaid",              source = "req.isPaid")
    @Mapping(target = "notes",               source = "req.notes")
    @Mapping(target = "paymentMethodId",     source = "req.paymentMethodId")
    @Mapping(target = "scheduledPayAt",      source = "req.scheduledPayAt")
    @Mapping(target = "reminderHoursBefore", source = "req.reminderHoursBefore")
    UpdateBudgetItemCommand toUpdateCommand(UpdateBudgetItemRequest req, UUID tripId, UUID itemId);
}
