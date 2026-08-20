package com.travelapp.web.controllers;

import com.travelapp.sync.usecases.DeltaSyncUseCase;
import com.travelapp.web.dto.response.ExpenseResponse;
import com.travelapp.web.dto.response.SyncDeleted;
import com.travelapp.web.dto.response.SyncResponse;
import com.travelapp.web.dto.response.SyncUpdated;
import com.travelapp.web.dto.response.TripResponse;
import com.travelapp.web.dto.response.WishlistItemResponse;
import com.travelapp.web.mappers.ExpenseDtoMapper;
import com.travelapp.web.mappers.TripDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SyncController {

    private final DeltaSyncUseCase deltaSync;
    private final TripDtoMapper    tripMapper;
    private final ExpenseDtoMapper expenseMapper;

    @GetMapping("/sync")
    public ResponseEntity<SyncResponse> sync(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var result = deltaSync.execute(userId, since);
        return ResponseEntity.ok(new SyncResponse(
            result.getSyncedAt(),
            new SyncUpdated(
                result.getUpdatedTrips().stream().map(tripMapper::toResponse).toList(),
                result.getUpdatedExpenses().stream().map(expenseMapper::toExpenseResponse).toList(),
                result.getUpdatedWishlistItems().stream().map(w -> new WishlistItemResponse(
                    w.getId(), w.getName(), w.getCategory(), w.getDestinationCity(),
                    w.getLatitude(), w.getLongitude(), w.getPriority(), null,
                    w.getEstimatedCost(), w.getWebsiteUrl(), w.getConvertedToEventId()
                )).toList()
            ),
            new SyncDeleted(
                result.getDeletedTripIds(),
                result.getDeletedExpenseIds(),
                result.getDeletedWishlistItemIds()
            )
        ));
    }
}
