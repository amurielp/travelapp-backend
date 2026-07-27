package com.travelapp.web.controllers;

import com.travelapp.budget.usecases.*;
import com.travelapp.web.dto.request.CreateBudgetItemRequest;
import com.travelapp.web.dto.request.UpdateBudgetItemRequest;
import com.travelapp.web.dto.response.BudgetCategorySummary;
import com.travelapp.web.dto.response.BudgetItemResponse;
import com.travelapp.web.dto.response.BudgetResponse;
import com.travelapp.web.mappers.BudgetDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final GetBudgetUseCase         getBudget;
    private final AddBudgetItemUseCase     addItem;
    private final UpdateBudgetItemUseCase  updateItem;
    private final DeleteBudgetItemUseCase  deleteItem;
    private final GetBudgetSummaryUseCase  getSummary;
    private final GetBudgetTimelineUseCase getTimeline;
    private final BudgetDtoMapper          mapper;

    @GetMapping
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(mapper.toResponse(getBudget.execute(tripId)));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<BudgetCategorySummary>> getSummary(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        var summaries = getSummary.execute(tripId).stream()
            .map(mapper::toCategorySummary)
            .toList();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<BudgetItemResponse>> getTimeline(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        var items = getTimeline.execute(tripId).stream()
            .map(mapper::toItemResponse)
            .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<BudgetItemResponse> addItem(
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateBudgetItemRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = mapper.toAddCommand(req, tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toItemResponse(addItem.execute(cmd)));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<BudgetItemResponse> updateItem(
            @PathVariable UUID tripId,
            @PathVariable UUID itemId,
            @RequestBody UpdateBudgetItemRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = mapper.toUpdateCommand(req, tripId, itemId);
        return ResponseEntity.ok(mapper.toItemResponse(updateItem.execute(cmd)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID tripId,
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt) {
        deleteItem.execute(itemId);
        return ResponseEntity.noContent().build();
    }
}
