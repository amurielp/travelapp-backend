package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "suggestions", indexes = {
    @Index(name = "idx_suggestions_trip_date", columnList = "trip_id, date")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuggestionEntity {

    @Id
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String name;

    @Column
    private String category;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String reasoning;

    @Column(name = "estimated_duration_min")
    private Integer estimatedDurationMin;

    @Column(name = "estimated_cost_eur")
    private Double estimatedCostEur;

    @Column(name = "best_time_of_day")
    private String bestTimeOfDay;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column
    private String source;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
