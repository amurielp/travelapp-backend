package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.UserConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserConsentJpaRepository extends JpaRepository<UserConsentEntity, UUID> {
    Optional<UserConsentEntity> findByUserId(UUID userId);
}
