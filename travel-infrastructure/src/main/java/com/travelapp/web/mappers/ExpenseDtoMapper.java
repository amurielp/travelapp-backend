package com.travelapp.web.mappers;

import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.expenses.usecase.AddExpenseCommand;
import com.travelapp.expenses.usecase.ExpenseSummaryResult;
import com.travelapp.expenses.usecase.UpdateExpenseCommand;
import com.travelapp.web.dto.request.CreateExpenseRequest;
import com.travelapp.web.dto.request.UpdateExpenseRequest;
import com.travelapp.web.dto.response.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseDtoMapper {

    @Mapping(target = "category",      expression = "java(expense.getCategory() != null ? expense.getCategory().name() : null)")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "eventTitle",    source = "eventTitle")
    @Mapping(target = "bookingStatus", source = "bookingStatus")
    ExpenseResponse toExpenseResponse(Expense expense);

    ExpenseSummaryResponse toSummaryResponse(ExpenseSummaryResult result);

    @Mapping(target = "category",   expression = "java(cs.category().name())")
    @Mapping(target = "total",      source = "totalAmount")
    @Mapping(target = "numItems",   source = "numItems")
    @Mapping(target = "numPaid",    source = "numPaid")
    ExpenseCategorySummary toCategorySummary(ExpenseRepository.CategorySummary cs);

    @Mapping(target = "tripId",              source = "tripId")
    @Mapping(target = "eventId",             source = "req.eventId")
    @Mapping(target = "description",         source = "req.description")
    @Mapping(target = "amount",              source = "req.amount")
    @Mapping(target = "currency",            source = "req.currency")
    @Mapping(target = "category",            expression = "java(ExpenseCategory.valueOf(req.category().toUpperCase()))")
    @Mapping(target = "isPaid",              source = "req.isPaid")
    @Mapping(target = "paidAt",              source = "req.paidAt")
    @Mapping(target = "notes",               source = "req.notes")
    @Mapping(target = "paymentMethodId",     source = "req.paymentMethodId")
    @Mapping(target = "scheduledPayAt",      source = "req.scheduledPayAt")
    @Mapping(target = "reminderHoursBefore", source = "req.reminderHoursBefore")
    AddExpenseCommand toAddCommand(CreateExpenseRequest req, UUID tripId);

    @Mapping(target = "itemId",              source = "itemId")
    @Mapping(target = "tripId",              source = "tripId")
    @Mapping(target = "description",         source = "req.description")
    @Mapping(target = "amount",              source = "req.amount")
    @Mapping(target = "isPaid",              source = "req.isPaid")
    @Mapping(target = "paidAt",              source = "req.paidAt")
    @Mapping(target = "notes",               source = "req.notes")
    @Mapping(target = "paymentMethodId",     source = "req.paymentMethodId")
    @Mapping(target = "scheduledPayAt",      source = "req.scheduledPayAt")
    @Mapping(target = "reminderHoursBefore", source = "req.reminderHoursBefore")
    UpdateExpenseCommand toUpdateCommand(UpdateExpenseRequest req, UUID tripId, UUID itemId);
}
