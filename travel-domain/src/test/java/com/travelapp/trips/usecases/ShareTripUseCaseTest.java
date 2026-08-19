package com.travelapp.trips.usecases;

import com.travelapp.shared.exceptions.TripNotFoundException;
import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareTripUseCaseTest {

    @Mock TripRepository tripRepository;
    @InjectMocks ShareTripUseCase useCase;

    private final UUID tripId = UUID.randomUUID();

    @Test
    void execute_tripWithoutPublicSlug_deepLinkContainsTripId_webUrlEqualsDeepLink() {
        Trip trip = Trip.builder().id(tripId).ownerId(UUID.randomUUID()).title("Rome").build();
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        TripShareResult result = useCase.execute(tripId);

        assertThat(result.deepLink()).contains(tripId.toString());
        assertThat(result.webUrl()).isEqualTo(result.deepLink());
    }

    @Test
    void execute_tripWithPublicSlug_webUrlContainsSlug() {
        Trip trip = Trip.builder().id(tripId).ownerId(UUID.randomUUID()).title("Rome")
                .publicSlug("rome-adventure-2025").build();
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        TripShareResult result = useCase.execute(tripId);

        assertThat(result.webUrl()).contains("rome-adventure-2025");
    }

    @Test
    void execute_tripWithPublicSlug_webUrlDiffersFromDeepLink() {
        Trip trip = Trip.builder().id(tripId).ownerId(UUID.randomUUID()).title("Rome")
                .publicSlug("rome-adventure-2025").build();
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        TripShareResult result = useCase.execute(tripId);

        assertThat(result.webUrl()).isNotEqualTo(result.deepLink());
    }

    @Test
    void execute_tripNotFound_throwsTripNotFoundException() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(tripId))
                .isInstanceOf(TripNotFoundException.class);
    }
}
