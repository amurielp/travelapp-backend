package com.travelapp.trips.domain;

import com.travelapp.shared.domain.AggregateRoot;
import com.travelapp.shared.valueobjects.Money;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @AllArgsConstructor
public class Trip extends AggregateRoot<UUID> {

    private final UUID        id;
    private final UUID        ownerId;
    private String            title;
    private String            description;
    private String            coverImageUrl;
    private TripStatus        status;
    private LocalDate         startDate;
    private LocalDate         endDate;
    private String            baseCurrency;
    private boolean           isPublic;
    private String            publicSlug;

    public void updateDetails(String title, String description) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        this.title = title;
        this.description = description;
    }

    public void publish(String slug) {
        this.isPublic  = true;
        this.publicSlug = slug;
    }

    public void archive() {
        this.status = TripStatus.ARCHIVED;
    }

    public boolean isOngoing() {
        var today = LocalDate.now();
        return !startDate.isAfter(today) && !endDate.isBefore(today);
    }
}
