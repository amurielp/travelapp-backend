package com.travelapp.web.controllers;

import com.travelapp.users.usecases.GetOrCreateUserUseCase;
import com.travelapp.users.usecases.UpdatePreferencesUseCase;
import com.travelapp.web.dto.request.UserPreferencesRequest;
import com.travelapp.web.dto.response.UserResponse;
import com.travelapp.web.mappers.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final GetOrCreateUserUseCase   getOrCreate;
    private final UpdatePreferencesUseCase updatePrefs;
    private final UserDtoMapper            mapper;

    @GetMapping
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        var user = getOrCreate.execute(jwt.getSubject(),
            jwt.getClaimAsString("email"), jwt.getClaimAsString("name"));
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserResponse> updatePreferences(
            @RequestBody UserPreferencesRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var prefs  = mapper.toDomain(req);
        return ResponseEntity.ok(mapper.toResponse(updatePrefs.execute(userId, prefs)));
    }
}
