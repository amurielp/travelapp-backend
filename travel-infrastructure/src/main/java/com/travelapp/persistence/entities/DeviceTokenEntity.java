package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "device_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceTokenEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String platform;

    @Column(name = "fcm_token", unique = true, nullable = false)
    private String fcmToken;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "registered_at", updatable = false)
    private OffsetDateTime registeredAt;

    @Column(name = "last_seen_at")
    private OffsetDateTime lastSeenAt;

    @PrePersist
    void onCreate() {
        if (registeredAt == null) registeredAt = OffsetDateTime.now();
        if (lastSeenAt == null)   lastSeenAt   = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        lastSeenAt = OffsetDateTime.now();
    }
}
