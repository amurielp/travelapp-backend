package com.travelapp.web.controllers;

import com.travelapp.notifications.usecases.RegisterDeviceTokenCommand;
import com.travelapp.notifications.usecases.RegisterDeviceTokenUseCase;
import com.travelapp.notifications.usecases.RevokeDeviceTokenUseCase;
import com.travelapp.web.dto.request.RegisterDeviceTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
public class NotificationsController {

    private final RegisterDeviceTokenUseCase registerDeviceToken;
    private final RevokeDeviceTokenUseCase   revokeDeviceToken;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerToken(
            @Valid @RequestBody RegisterDeviceTokenRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        registerDeviceToken.execute(new RegisterDeviceTokenCommand(
            userId,
            req.platform(),
            req.fcmToken(),
            req.deviceModel(),
            req.appVersion()
        ));
    }

    @DeleteMapping("/{platform}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeToken(
            @PathVariable String platform,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        revokeDeviceToken.execute(userId, platform);
    }
}
