package com.travelapp.persistence.repositories;

import com.travelapp.notifications.repository.UserTokenRepository;
import com.travelapp.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserTokenRepositoryAdapter implements UserTokenRepository {

    private final UserJpaRepository userJpa;

    @Override
    public Optional<String> findEmail(UUID userId) {
        return userJpa.findById(userId).map(UserEntity::getEmail);
    }

    @Override
    public Optional<String> findFcmToken(UUID userId) {
        return Optional.empty();
    }
}
