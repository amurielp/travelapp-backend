package com.travelapp.events.domain;

import com.travelapp.events.usecases.UpdateEventCommand;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TravelEventTest {

    private TravelEvent event(OffsetDateTime start, OffsetDateTime end) {
        return TravelEvent.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .type(EventType.ACTIVITY).title("Test")
            .startDatetime(start).endDatetime(end)
            .allDay(false).timezone(ZoneId.of("Europe/Madrid"))
            .status(EventStatus.CONFIRMED).source(EventSource.MANUAL)
            .build();
    }

    private OffsetDateTime dt(int hour) {
        return OffsetDateTime.of(2025, 6, 15, hour, 0, 0, 0, ZoneOffset.UTC);
    }

    @Test
    void confirm_setsStatusConfirmed() {
        var e = event(dt(10), dt(12));
        e.cancel();
        e.confirm();
        assertThat(e.getStatus()).isEqualTo(EventStatus.CONFIRMED);
    }

    @Test
    void cancel_setsStatusCancelled() {
        var e = event(dt(10), dt(12));
        e.cancel();
        assertThat(e.getStatus()).isEqualTo(EventStatus.CANCELLED);
    }

    @Test
    void overlapsWith_overlappingEvents_returnsTrue() {
        var a = event(dt(10), dt(14));
        var b = event(dt(12), dt(16));
        assertThat(a.overlapsWith(b)).isTrue();
    }

    @Test
    void overlapsWith_nonOverlappingEvents_returnsFalse() {
        var a = event(dt(9), dt(11));
        var b = event(dt(11), dt(13));
        assertThat(a.overlapsWith(b)).isFalse();
    }

    @Test
    void overlapsWith_nullEndtime_returnsFalse() {
        var a = event(dt(10), null);
        var b = event(dt(12), dt(14));
        assertThat(a.overlapsWith(b)).isFalse();
    }

    @Test
    void update_updatesNonNullFields() {
        var e = event(dt(10), dt(12));
        var cmd = new UpdateEventCommand(
            e.getId(), e.getTripId(),
            "New Title", "Some notes", null,
            dt(9), dt(11),
            null, null, null, null,
            null, null, null, null
        );
        e.update(cmd);
        assertThat(e.getTitle()).isEqualTo("New Title");
        assertThat(e.getNotes()).isEqualTo("Some notes");
        assertThat(e.getStartDatetime()).isEqualTo(dt(9));
        assertThat(e.getColor()).isNull();
    }

    @Test
    void update_nullFieldsNotOverwritten() {
        var e = event(dt(10), dt(12));
        var originalTitle = e.getTitle();
        var cmd = new UpdateEventCommand(
            e.getId(), e.getTripId(),
            null, null, null, null, null,
            null, null, null, null,
            null, null, null, null
        );
        e.update(cmd);
        assertThat(e.getTitle()).isEqualTo(originalTitle);
    }
}
