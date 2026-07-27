package com.travelapp.web.controllers;

import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import com.travelapp.web.dto.request.CreateWishlistItemRequest;
import com.travelapp.wishlist.ports.WishlistRepository;
import com.travelapp.wishlist.usecases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final AddToWishlistUseCase      addToWishlist;
    private final WishlistRepository        wishlistRepo;
    private final ValidateTripAccessUseCase validateAccess;

    @GetMapping
    public ResponseEntity<?> list(@PathVariable UUID tripId, @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(wishlistRepo.findByTripId(tripId));
    }

    @PostMapping
    public ResponseEntity<?> add(
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateWishlistItemRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(addToWishlist.execute(new AddToWishlistCommand(
                tripId, req.name(), req.category(), req.destinationCity(),
                req.latitude(), req.longitude(), req.externalPlaceId(),
                req.priority(), req.estimatedCost(), req.websiteUrl())));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tripId,
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        wishlistRepo.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }
}
