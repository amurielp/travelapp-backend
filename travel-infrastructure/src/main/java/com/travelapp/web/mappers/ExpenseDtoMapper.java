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
import java.math.RoundingMode;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseDtoMapper {

    @Mapping(target = "category",      expression = "java(expense.getCategory() != null ? expense.getCategory().name() : null)")
    @Mapping(target = "isPaid",        source = "paid")
    @Mapping(target = "eventTitle",    source = "eventTitle")
    @Mapping(target = "bookingStatus", source = "bookingStatus")
    ExpenseResponse toExpenseResponse(Expense expense);

    ExpenseSummaryResponse toSummaryResponse(ExpenseSummaryResult result);

    @Mapping(target = "category",        expression = "java(cs.category().name())")
    @Mapping(target = "totalEstimated",  source = "totalEstimated")
    @Mapping(target = "totalActual",     source = "totalActual")
    @Mapping(target = "numItems",        source = "numItems")
    @Mapping(target = "numPaid",         source = "numPaid")
    @Mapping(target = "percentageUsed",  expression = "java(computePercentage(cs.totalEstimated(), cs.totalActual()))")
    ExpenseCategorySummary toCategorySummary(ExpenseRepository.CategorySummary cs);

    @Mapping(target = "tripId",              source = "tripId")
    @Mapping(target = "eventId",             source = "req.eventId")
    @Mapping(target = "description",         source = "req.description")
    @Mapping(target = "amountEstimated",     source = "req.amountEstimated")
    @Mapping(target = "currency",            source = "req.currency")
    @Mapping(target = "category",            expression = "java(ExpenseCategory.valueOf(req.category().toUpperCase()))")
    @Mapping(target = "notes",               source = "req.notes")
    @Mapping(target = "paymentMethodId",     source = "req.paymentMethodId")
    @Mapping(target = "scheduledPayAt",      source = "req.scheduledPayAt")
    @Mapping(target = "reminderHoursBefore", source = "req.reminderHoursBefore")
    AddExpenseCommand toAddCommand(CreateExpenseRequest req, UUID tripId);

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
    UpdateExpenseCommand toUpdateCommand(UpdateExpenseRequest req, UUID tripId, UUID itemId);

    default BigDecimal computePercentage(BigDecimal estimated, BigDecimal actual) {
        if (estimated == null || estimated.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal act = actual != null ? actual : BigDecimal.ZERO;
        return act.multiply(BigDecimal.valueOf(100)).divide(estimated, 2, RoundingMode.HALF_UP);
    }
}
