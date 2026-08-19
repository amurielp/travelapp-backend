package com.travelapp.users.usecases;

import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.ports.UserConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserConsentUseCase {

    private final UserConsentRepository consentRepository;

    @Transactional
    public UserConsent execute(UpdateUserConsentCommand cmd) {
        UserConsent existing = consentRepository.findByUserId(cmd.userId()).orElse(null);
        OffsetDateTime consentedAt = existing != null ? existing.getConsentedAt() : OffsetDateTime.now();

        UserConsent consent = UserConsent.builder()
            .userId(cmd.userId())
            .adsPersonalized(cmd.adsPersonalized())
            .analytics(cmd.analytics())
            .consentVersion(cmd.consentVersion())
            .consentedAt(consentedAt)
            .build();

        return consentRepository.save(consent);
    }
}
