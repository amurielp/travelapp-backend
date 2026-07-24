package com.travelapp.web.controllers;

import com.travelapp.budget.usecases.AddBudgetItemUseCase;
import com.travelapp.budget.usecases.GetBudgetUseCase;
import com.travelapp.web.dto.request.CreateBudgetItemRequest;
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

    private final GetBudgetUseCase    getBudget;
    private final AddBudgetItemUseCase addItem;
    private final BudgetDtoMapper     mapper;

    @GetMapping
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(mapper.toResponse(getBudget.execute(tripId)));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<BudgetCategorySummary>> getBudgetSummary(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/items")
    public ResponseEntity<BudgetItemResponse> addBudgetItem(
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateBudgetItemRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = mapper.toCommand(req, tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toItemResponse(addItem.execute(cmd)));
    }
}
