package com.travelapp.persistence.repositories;

import com.travelapp.persistence.mappers.UserConsentMapper;
import com.travelapp.users.domain.UserConsent;
import com.travelapp.users.ports.UserConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserConsentRepositoryAdapter implements UserConsentRepository {

    private final UserConsentJpaRepository jpa;
    private final UserConsentMapper mapper;

    @Override
    public UserConsent save(UserConsent consent) {
        return mapper.toDomain(jpa.save(mapper.toEntity(consent)));
    }

    @Override
    public Optional<UserConsent> findByUserId(UUID userId) {
        return jpa.findByUserId(userId).map(mapper::toDomain);
    }
}
