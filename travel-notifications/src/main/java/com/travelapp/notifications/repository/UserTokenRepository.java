package com.travelapp.notifications.repository;

import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository {
    Optional<String> findFcmToken(UUID userId);
    Optional<String> findEmail(UUID userId);
}
