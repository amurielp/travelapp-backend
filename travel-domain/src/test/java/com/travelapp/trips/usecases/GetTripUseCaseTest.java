package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTripUseCaseTest {

    @Mock TripRepository trips;
    @InjectMocks GetTripUseCase useCase;

    @Test
    void execute_found_returnsTrip() {
        var id = UUID.randomUUID();
        var trip = Trip.builder().id(id).ownerId(UUID.randomUUID())
            .title("Paris").status(TripStatus.PLANNING).build();
        when(trips.findById(id)).thenReturn(Optional.of(trip));

        var result = useCase.execute(id);

        assertThat(result).isEqualTo(trip);
    }

    @Test
    void execute_notFound_throwsTripNotFoundException() {
        var id = UUID.randomUUID();
        when(trips.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
            .isInstanceOf(TripNotFoundException.class);
    }
}
