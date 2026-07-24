package com.travelapp.gaps.domain;

import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class TripGap extends AggregateRoot<UUID> {
    private final UUID      id;
    private final UUID      tripId;
    private final GapType   gapType;
    private final GapSeverity severity;
    private final LocalDate affectedFrom;
    private final LocalDate affectedTo;
    private final String    originCity;
    private final String    originIata;
    private final String    destinationCity;
    private final String    destinationIata;
    private final UUID      eventIdFrom;
    private final UUID      eventIdTo;
    private GapStatus       status;
    private String          ignoredReason;
    private OffsetDateTime  snoozedUntil;
    private String          suggestionText;
    private final OffsetDateTime detectedAt;
    private OffsetDateTime  resolvedAt;

    public void resolve() {
        this.status      = GapStatus.RESOLVED;
        this.resolvedAt  = OffsetDateTime.now();
    }

    public void ignore(String reason) {
        this.status        = GapStatus.IGNORED;
        this.ignoredReason = reason;
        this.resolvedAt    = OffsetDateTime.now();
    }

    public void snooze(OffsetDateTime until) {
        this.status       = GapStatus.SNOOZED;
        this.snoozedUntil = until;
    }

    public boolean isActive() {
        if (status == GapStatus.RESOLVED || status == GapStatus.IGNORED) return false;
        if (status == GapStatus.SNOOZED && snoozedUntil != null && OffsetDateTime.now().isBefore(snoozedUntil)) return false;
        return true;
    }
}
