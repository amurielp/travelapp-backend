package com.travelapp.gaps.domain;

import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TripGapTest {

    private TripGap open() {
        return TripGap.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .gapType(GapType.NO_TRANSPORT_BETWEEN_DESTINATIONS)
            .severity(GapSeverity.WARNING)
            .affectedFrom(LocalDate.now()).affectedTo(LocalDate.now().plusDays(1))
            .status(GapStatus.OPEN)
            .detectedAt(OffsetDateTime.now())
            .build();
    }

    @Test
    void resolve_setsStatusResolvedAndTimestamp() {
        var gap = open();
        gap.resolve();
        assertThat(gap.getStatus()).isEqualTo(GapStatus.RESOLVED);
        assertThat(gap.getResolvedAt()).isNotNull();
    }

    @Test
    void ignore_setsStatusIgnoredWithReason() {
        var gap = open();
        gap.ignore("Tengo coche propio");
        assertThat(gap.getStatus()).isEqualTo(GapStatus.IGNORED);
        assertThat(gap.getIgnoredReason()).isEqualTo("Tengo coche propio");
        assertThat(gap.getResolvedAt()).isNotNull();
    }

    @Test
    void snooze_setsStatusSnoozedWithDate() {
        var gap = open();
        var until = OffsetDateTime.now().plusDays(7);
        gap.snooze(until);
        assertThat(gap.getStatus()).isEqualTo(GapStatus.SNOOZED);
        assertThat(gap.getSnoozedUntil()).isEqualTo(until);
    }

    @Test
    void isActive_whenOpen_returnsTrue() {
        assertThat(open().isActive()).isTrue();
    }

    @Test
    void isActive_whenResolved_returnsFalse() {
        var gap = open();
        gap.resolve();
        assertThat(gap.isActive()).isFalse();
    }

    @Test
    void isActive_whenIgnored_returnsFalse() {
        var gap = open();
        gap.ignore("reason");
        assertThat(gap.isActive()).isFalse();
    }

    @Test
    void isActive_whenSnoozedAndFutureDate_returnsFalse() {
        var gap = open();
        gap.snooze(OffsetDateTime.now().plusDays(3));
        assertThat(gap.isActive()).isFalse();
    }

    @Test
    void isActive_whenSnoozedAndPastDate_returnsTrue() {
        var gap = open();
        gap.snooze(OffsetDateTime.now().minusMinutes(1));
        assertThat(gap.isActive()).isTrue();
    }
}
