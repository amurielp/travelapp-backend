package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "trip_gaps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripGapEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "gap_type", nullable = false)
    private String gapType;

    @Column(nullable = false)
    private String severity;

    @Column(name = "affected_from", nullable = false)
    private LocalDate affectedFrom;

    @Column(name = "affected_to")
    private LocalDate affectedTo;

    @Column(name = "origin_city")
    private String originCity;

    @Column(name = "origin_iata")
    private String originIata;

    @Column(name = "destination_city")
    private String destinationCity;

    @Column(name = "destination_iata")
    private String destinationIata;

    @Column(name = "event_id_from")
    private UUID eventIdFrom;

    @Column(name = "event_id_to")
    private UUID eventIdTo;

    @Column(nullable = false)
    private String status;

    @Column(name = "ignored_reason")
    private String ignoredReason;

    @Column(name = "snoozed_until")
    private OffsetDateTime snoozedUntil;

    @Column(name = "suggestion_text")
    private String suggestionText;

    @Column(name = "detected_at", updatable = false, nullable = false)
    private OffsetDateTime detectedAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @PrePersist
    void onCreate() { if (detectedAt == null) detectedAt = OffsetDateTime.now(); }
}
