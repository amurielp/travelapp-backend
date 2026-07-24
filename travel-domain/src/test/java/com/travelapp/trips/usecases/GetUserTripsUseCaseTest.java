package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserTripsUseCaseTest {

    @Mock TripRepository trips;
    @InjectMocks GetUserTripsUseCase useCase;

    @Test
    void execute_returnsTripsForUser() {
        var userId = UUID.randomUUID();
        var trip = Trip.builder().id(UUID.randomUUID()).ownerId(userId)
            .title("T").status(TripStatus.PLANNING)
            .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(3))
            .baseCurrency("EUR").build();
        when(trips.findByOwnerId(userId)).thenReturn(List.of(trip));

        var result = useCase.execute(userId);

        assertThat(result).containsExactly(trip);
    }

    @Test
    void execute_noTrips_returnsEmptyList() {
        var userId = UUID.randomUUID();
        when(trips.findByOwnerId(userId)).thenReturn(List.of());
        assertThat(useCase.execute(userId)).isEmpty();
    }
}
