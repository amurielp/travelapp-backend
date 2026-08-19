package com.travelapp.web.controllers;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.usecases.GetSubscriptionUseCase;
import com.travelapp.subscriptions.usecases.VerifyReceiptCommand;
import com.travelapp.subscriptions.usecases.VerifyReceiptUseCase;
import com.travelapp.web.dto.request.VerifyReceiptRequest;
import com.travelapp.web.dto.response.SubscriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final GetSubscriptionUseCase getSubscription;
    private final VerifyReceiptUseCase   verifyReceipt;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        var sub = getSubscription.execute(UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(toResponse(sub));
    }

    @PostMapping("/verify")
    public ResponseEntity<SubscriptionResponse> verify(
            @Valid @RequestBody VerifyReceiptRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var cmd = new VerifyReceiptCommand(UUID.fromString(jwt.getSubject()),
                req.store(), req.receiptData(), req.productId());
        return ResponseEntity.ok(toResponse(verifyReceipt.execute(cmd)));
    }

    private SubscriptionResponse toResponse(Subscription sub) {
        return new SubscriptionResponse(sub.getPlanId(), sub.getStatus(), sub.getStore(),
                sub.isAutoRenew(), sub.getStartedAt(), sub.getExpiresAt(), sub.getTrialEnd());
    }
}
