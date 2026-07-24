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
class DeleteTripUseCaseTest {

    @Mock TripRepository trips;
    @InjectMocks DeleteTripUseCase useCase;

    @Test
    void execute_found_deletesTrip() {
        var id = UUID.randomUUID();
        var trip = Trip.builder().id(id).ownerId(UUID.randomUUID())
            .title("T").status(TripStatus.PLANNING).build();
        when(trips.findById(id)).thenReturn(Optional.of(trip));

        useCase.execute(id);

        verify(trips).deleteById(id);
    }

    @Test
    void execute_notFound_throwsTripNotFoundException() {
        var id = UUID.randomUUID();
        when(trips.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
            .isInstanceOf(TripNotFoundException.class);
        verify(trips, never()).deleteById(any());
    }
}
