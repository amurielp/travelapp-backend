package com.travelapp.trips.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TripTest {

    private Trip newTrip(LocalDate start, LocalDate end) {
        return Trip.builder()
            .id(UUID.randomUUID())
            .ownerId(UUID.randomUUID())
            .title("Test Trip")
            .status(TripStatus.PLANNING)
            .startDate(start)
            .endDate(end)
            .baseCurrency("EUR")
            .isPublic(false)
            .build();
    }

    @Test
    void updateDetails_withBlankTitle_throws() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(7));
        assertThatThrownBy(() -> trip.updateDetails("  ", "desc"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateDetails_withNullTitle_throws() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(7));
        assertThatThrownBy(() -> trip.updateDetails(null, "desc"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateDetails_updatesFields() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(7));
        trip.updateDetails("New Title", "New Desc");
        assertThat(trip.getTitle()).isEqualTo("New Title");
        assertThat(trip.getDescription()).isEqualTo("New Desc");
    }

    @Test
    void publish_setsPublicAndSlug() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(5));
        trip.publish("my-trip-2024");
        assertThat(trip.isPublic()).isTrue();
        assertThat(trip.getPublicSlug()).isEqualTo("my-trip-2024");
    }

    @Test
    void archive_setsStatusArchived() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(5));
        trip.archive();
        assertThat(trip.getStatus()).isEqualTo(TripStatus.ARCHIVED);
    }

    @Test
    void isOngoing_whenTodayIsWithinRange_returnsTrue() {
        var today = LocalDate.now();
        var trip = newTrip(today.minusDays(2), today.plusDays(2));
        assertThat(trip.isOngoing()).isTrue();
    }

    @Test
    void isOngoing_whenPast_returnsFalse() {
        var trip = newTrip(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
        assertThat(trip.isOngoing()).isFalse();
    }

    @Test
    void isOngoing_whenFuture_returnsFalse() {
        var trip = newTrip(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        assertThat(trip.isOngoing()).isFalse();
    }

    @Test
    void setters_updateMutableFields() {
        var trip = newTrip(LocalDate.now(), LocalDate.now().plusDays(3));
        trip.setTitle("Updated");
        trip.setBaseCurrency("USD");
        assertThat(trip.getTitle()).isEqualTo("Updated");
        assertThat(trip.getBaseCurrency()).isEqualTo("USD");
    }
}
