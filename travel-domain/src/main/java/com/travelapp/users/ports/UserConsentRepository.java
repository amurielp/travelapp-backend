package com.travelapp.users.ports;

import com.travelapp.users.domain.UserConsent;

import java.util.Optional;
import java.util.UUID;

public interface UserConsentRepository {
    UserConsent save(UserConsent consent);
    Optional<UserConsent> findByUserId(UUID userId);
}
