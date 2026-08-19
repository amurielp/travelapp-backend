package com.travelapp.users.usecases;

import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.ports.UserConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserConsentUseCase {

    private final UserConsentRepository consentRepository;

    @Transactional(readOnly = true)
    public UserConsent execute(UUID userId) {
        return consentRepository.findByUserId(userId).orElseGet(() ->
            UserConsent.builder()
                .userId(userId)
                .adsPersonalized(false)
                .analytics(false)
                .consentVersion("1.0")
                .consentedAt(null)
                .build());
    }
}
