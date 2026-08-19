package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenEntity, UUID> {

    Optional<DeviceTokenEntity> findByUserIdAndPlatformAndActiveTrue(UUID userId, String platform);

    @Modifying
    @Query("UPDATE DeviceTokenEntity d SET d.active = false WHERE d.userId = :userId AND d.platform = :platform")
    void deactivateByUserIdAndPlatform(@Param("userId") UUID userId, @Param("platform") String platform);
}
