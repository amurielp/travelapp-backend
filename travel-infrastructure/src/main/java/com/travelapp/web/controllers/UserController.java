package com.travelapp.web.controllers;

import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.domain.UserFeatures;
import com.travelapp.users.usecases.GetOrCreateUserUseCase;
import com.travelapp.users.usecases.GetUserConsentUseCase;
import com.travelapp.users.usecases.GetUserFeaturesUseCase;
import com.travelapp.users.usecases.RegisterRewardedUnlockCommand;
import com.travelapp.users.usecases.RegisterRewardedUnlockUseCase;
import com.travelapp.users.usecases.UpdatePreferencesUseCase;
import com.travelapp.users.usecases.UpdateUserConsentCommand;
import com.travelapp.users.usecases.UpdateUserConsentUseCase;
import com.travelapp.web.dto.request.RewardedUnlockRequest;
import com.travelapp.web.dto.request.UpdateConsentRequest;
import com.travelapp.web.dto.request.UserPreferencesRequest;
import com.travelapp.web.dto.response.ConsentResponse;
import com.travelapp.web.dto.response.RewardedUnlockResponse;
import com.travelapp.web.dto.response.UserFeaturesResponse;
import com.travelapp.web.dto.response.UserResponse;
import com.travelapp.web.mappers.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final GetOrCreateUserUseCase         getOrCreate;
    private final UpdatePreferencesUseCase       updatePrefs;
    private final UserDtoMapper                  mapper;
    private final GetUserFeaturesUseCase         getFeatures;
    private final GetUserConsentUseCase          getConsent;
    private final UpdateUserConsentUseCase       updateConsent;
    private final RegisterRewardedUnlockUseCase  rewardedUnlock;

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

    @GetMapping("/features")
    public ResponseEntity<UserFeaturesResponse> getFeatures(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        UserFeatures features = getFeatures.execute(userId);
        List<RewardedUnlockResponse> unlockResponses = features.getRewardedUnlocks().stream()
            .map(u -> new RewardedUnlockResponse(u.getFeatureKey(), u.getUnlockedAt(), u.getExpiresAt()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(new UserFeaturesResponse(
            features.isShowAds(),
            features.isAiSuggestions(),
            features.isAiPdfParsing(),
            features.isMultiTraveler(),
            features.getPlan(),
            features.getPlanExpiresAt(),
            unlockResponses
        ));
    }

    @GetMapping("/consent")
    public ResponseEntity<ConsentResponse> getConsent(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        UserConsent consent = getConsent.execute(userId);
        return ResponseEntity.ok(new ConsentResponse(
            consent.isAdsPersonalized(),
            consent.isAnalytics(),
            consent.getConsentVersion(),
            consent.getConsentedAt()
        ));
    }

    @PutMapping("/consent")
    public ResponseEntity<ConsentResponse> updateConsent(
            @Valid @RequestBody UpdateConsentRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        UserConsent consent = updateConsent.execute(new UpdateUserConsentCommand(
            userId,
            req.adsPersonalized(),
            req.analytics(),
            req.consentVersion()
        ));
        return ResponseEntity.ok(new ConsentResponse(
            consent.isAdsPersonalized(),
            consent.isAnalytics(),
            consent.getConsentVersion(),
            consent.getConsentedAt()
        ));
    }

    @PostMapping("/rewarded-unlock")
    public ResponseEntity<RewardedUnlockResponse> registerRewardedUnlock(
            @Valid @RequestBody RewardedUnlockRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        RewardedUnlock unlock = rewardedUnlock.execute(new RegisterRewardedUnlockCommand(
            userId,
            req.featureKey(),
            req.ttlHours()
        ));
        return ResponseEntity.ok(new RewardedUnlockResponse(
            unlock.getFeatureKey(),
            unlock.getUnlockedAt(),
            unlock.getExpiresAt()
        ));
    }
}
