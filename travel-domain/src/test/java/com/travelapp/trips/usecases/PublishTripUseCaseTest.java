package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishTripUseCaseTest {

    @Mock TripRepository trips;
    @InjectMocks PublishTripUseCase useCase;

    private Trip planningTrip(UUID id) {
        return Trip.builder().id(id).ownerId(UUID.randomUUID())
            .title("My Trip").status(TripStatus.PLANNING)
            .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(7))
            .baseCurrency("EUR").isPublic(false).build();
    }

    @Test
    void execute_planningTrip_setsPublicAndSlug() {
        var id = UUID.randomUUID();
        var trip = planningTrip(id);
        when(trips.findById(id)).thenReturn(Optional.of(trip));
        when(trips.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(id);

        assertThat(result.isPublic()).isTrue();
        assertThat(result.getPublicSlug()).isNotBlank();
    }

    @Test
    void execute_alreadyHasSlug_keepsSameSlug() {
        var id = UUID.randomUUID();
        var trip = planningTrip(id);
        trip.setPublicSlug("existing-slug");
        when(trips.findById(id)).thenReturn(Optional.of(trip));
        when(trips.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(id);

        assertThat(result.getPublicSlug()).isEqualTo("existing-slug");
    }

    @Test
    void execute_archivedTrip_throwsIllegalStateException() {
        var id = UUID.randomUUID();
        var trip = planningTrip(id);
        trip.archive();
        when(trips.findById(id)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> useCase.execute(id))
            .isInstanceOf(IllegalStateException.class);
        verify(trips, never()).save(any());
    }

    @Test
    void execute_notFound_throwsTripNotFoundException() {
        var id = UUID.randomUUID();
        when(trips.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
            .isInstanceOf(TripNotFoundException.class);
    }
}
