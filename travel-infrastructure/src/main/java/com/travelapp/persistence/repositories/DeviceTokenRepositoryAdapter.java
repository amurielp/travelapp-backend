package com.travelapp.persistence.repositories;

import com.travelapp.notifications.domain.DeviceToken;
import com.travelapp.notifications.ports.DeviceTokenRepository;
import com.travelapp.persistence.mappers.DeviceTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository @RequiredArgsConstructor
public class DeviceTokenRepositoryAdapter implements DeviceTokenRepository {

    private final DeviceTokenJpaRepository jpa;
    private final DeviceTokenMapper mapper;

    @Override
    public DeviceToken save(DeviceToken token) {
        return mapper.toDomain(jpa.save(mapper.toEntity(token)));
    }

    @Override
    public Optional<DeviceToken> findByUserIdAndPlatform(UUID userId, String platform) {
        return jpa.findByUserIdAndPlatformAndActiveTrue(userId, platform).map(mapper::toDomain);
    }

    @Override
    public void deactivateByUserIdAndPlatform(UUID userId, String platform) {
        jpa.deactivateByUserIdAndPlatform(userId, platform);
    }
}
