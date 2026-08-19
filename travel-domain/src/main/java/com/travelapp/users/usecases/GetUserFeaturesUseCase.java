package com.travelapp.users.usecases;

import com.travelapp.shared.exceptions.UserNotFoundException;
import com.travelapp.users.domain.RewardedUnlock;
import com.travelapp.users.domain.User;
import com.travelapp.users.domain.UserFeatures;
import com.travelapp.users.ports.RewardedUnlockRepository;
import com.travelapp.users.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserFeaturesUseCase {

    private final UserRepository userRepository;
    private final RewardedUnlockRepository unlockRepository;

    @Transactional(readOnly = true)
    public UserFeatures execute(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        OffsetDateTime now = OffsetDateTime.now();
        List<RewardedUnlock> unlocks = unlockRepository.findActiveByUserId(userId, now);

        boolean showAds;
        boolean aiSuggestions;
        boolean aiPdfParsing;
        boolean multiTraveler;

        switch (user.getPlan()) {
            case PREMIUM -> {
                showAds = false;
                aiSuggestions = true;
                aiPdfParsing = true;
                multiTraveler = false;
            }
            default -> {
                showAds = true;
                aiSuggestions = false;
                aiPdfParsing = false;
                multiTraveler = false;
            }
        }

        boolean hasAiSuggestionsUnlock = unlocks.stream()
            .anyMatch(u -> "ai_suggestions".equals(u.getFeatureKey()));
        if (hasAiSuggestionsUnlock) {
            aiSuggestions = true;
        }

        return UserFeatures.builder()
            .showAds(showAds)
            .aiSuggestions(aiSuggestions)
            .aiPdfParsing(aiPdfParsing)
            .multiTraveler(multiTraveler)
            .plan(user.getPlan().name().toLowerCase())
            .planExpiresAt(user.getPlanExpiresAt())
            .rewardedUnlocks(unlocks)
            .build();
    }
}
