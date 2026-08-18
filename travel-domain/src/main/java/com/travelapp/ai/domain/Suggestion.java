package com.travelapp.ai.domain;

import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Suggestion extends AggregateRoot<UUID> {

    private final UUID             id;
    private final UUID             tripId;
    private final LocalDate        date;
    private final String           name;
    private final String           category;
    private final String           description;
    private final String           reasoning;
    private final int              estimatedDurationMin;
    private final Double           estimatedCostEur;
    private final String           bestTimeOfDay;
    private final Double           latitude;
    private final Double           longitude;
    private final String           websiteUrl;
    private final String           source;
    private SuggestionStatus       status;
    private final OffsetDateTime   createdAt;

    public void accept()  { this.status = SuggestionStatus.ACCEPTED;  }
    public void dismiss() { this.status = SuggestionStatus.DISMISSED; }

    public static Suggestion from(UUID tripId, LocalDate date, ActivitySuggestion s) {
        return Suggestion.builder()
            .id(UUID.randomUUID())
            .tripId(tripId)
            .date(date)
            .name(s.name())
            .category(s.category())
            .description(s.description())
            .reasoning(s.reasoning())
            .estimatedDurationMin(s.estimatedDurationMin())
            .estimatedCostEur(s.estimatedCostEur())
            .bestTimeOfDay(s.bestTimeOfDay())
            .latitude(s.latitude())
            .longitude(s.longitude())
            .websiteUrl(s.websiteUrl())
            .source(s.source())
            .status(SuggestionStatus.PENDING)
            .createdAt(OffsetDateTime.now())
            .build();
    }
}
