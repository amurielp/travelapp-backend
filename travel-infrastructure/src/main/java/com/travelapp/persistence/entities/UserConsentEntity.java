package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_consent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConsentEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "ads_personalized", nullable = false)
    private boolean adsPersonalized;

    @Column(name = "analytics", nullable = false)
    private boolean analytics;

    @Column(name = "consent_version", nullable = false)
    private String consentVersion;

    @Column(name = "consented_at")
    private OffsetDateTime consentedAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
