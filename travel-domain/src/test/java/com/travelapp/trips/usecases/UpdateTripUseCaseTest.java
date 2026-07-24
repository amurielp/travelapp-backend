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
class UpdateTripUseCaseTest {

    @Mock TripRepository trips;
    @InjectMocks UpdateTripUseCase useCase;

    private Trip existingTrip(UUID id) {
        return Trip.builder()
            .id(id).ownerId(UUID.randomUUID()).title("Old Title")
            .startDate(LocalDate.of(2025, 1, 1)).endDate(LocalDate.of(2025, 1, 10))
            .baseCurrency("EUR").status(TripStatus.PLANNING).isPublic(false).build();
    }

    @Test
    void execute_updatesNonNullFields() {
        var id = UUID.randomUUID();
        var trip = existingTrip(id);
        when(trips.findById(id)).thenReturn(Optional.of(trip));
        when(trips.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new UpdateTripCommand(id, "New Title", null, null, null, "USD", null);
        var result = useCase.execute(cmd);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    void execute_nullFieldsNotOverwritten() {
        var id = UUID.randomUUID();
        var trip = existingTrip(id);
        when(trips.findById(id)).thenReturn(Optional.of(trip));
        when(trips.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new UpdateTripCommand(id, null, null, null, null, null, null);
        var result = useCase.execute(cmd);

        assertThat(result.getTitle()).isEqualTo("Old Title");
    }

    @Test
    void execute_notFound_throwsTripNotFoundException() {
        var id = UUID.randomUUID();
        when(trips.findById(id)).thenReturn(Optional.empty());

        var cmd = new UpdateTripCommand(id, "X", null, null, null, null, null);
        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(TripNotFoundException.class);
    }
}
