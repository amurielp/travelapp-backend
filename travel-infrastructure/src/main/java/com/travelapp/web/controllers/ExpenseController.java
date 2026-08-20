package com.travelapp.web.controllers;

import com.travelapp.expenses.usecase.*;
import com.travelapp.web.dto.request.CreateExpenseRequest;
import com.travelapp.web.dto.request.UpdateExpenseRequest;
import com.travelapp.web.dto.response.ExpenseCategorySummary;
import com.travelapp.web.dto.response.ExpenseResponse;
import com.travelapp.web.dto.response.ExpenseSummaryResponse;
import com.travelapp.web.mappers.ExpenseDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final GetExpenseSummaryUseCase         getSummary;
    private final GetExpenseCategorySummaryUseCase getCategorySummary;
    private final GetExpenseTimelineUseCase        getTimeline;
    private final AddExpenseUseCase                addExpense;
    private final UpdateExpenseUseCase             updateExpense;
    private final DeleteExpenseUseCase             deleteExpense;
    private final ExpenseDtoMapper                 mapper;

    @GetMapping
    public ResponseEntity<ExpenseSummaryResponse> getSummary(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(mapper.toSummaryResponse(getSummary.execute(tripId)));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ExpenseCategorySummary>> getCategorySummary(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        var summaries = getCategorySummary.execute(tripId).stream()
            .map(mapper::toCategorySummary)
            .toList();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<ExpenseResponse>> getTimeline(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        var items = getTimeline.execute(tripId).stream()
            .map(mapper::toExpenseResponse)
            .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateExpenseRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = mapper.toAddCommand(req, tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toExpenseResponse(addExpense.execute(cmd)));
    }

    @PatchMapping("/items/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable UUID tripId,
            @PathVariable UUID expenseId,
            @RequestBody UpdateExpenseRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = mapper.toUpdateCommand(req, tripId, expenseId);
        return ResponseEntity.ok(mapper.toExpenseResponse(updateExpense.execute(cmd)));
    }

    @DeleteMapping("/items/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable UUID tripId,
            @PathVariable UUID expenseId,
            @AuthenticationPrincipal Jwt jwt) {
        deleteExpense.execute(expenseId);
        return ResponseEntity.noContent().build();
    }
}
