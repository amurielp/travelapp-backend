package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTripUseCaseTest {

    @Mock TripRepository tripRepository;
    @InjectMocks CreateTripUseCase useCase;

    private final UUID ownerId = UUID.randomUUID();
    private final CreateTripCommand cmd = new CreateTripCommand(
        ownerId, "Tokyo 2025", "Golden Week trip",
        LocalDate.of(2025, 4, 29), LocalDate.of(2025, 5, 6), "EUR"
    );

    @Test
    void execute_savesAndReturnsTrip() {
        var expected = Trip.builder().id(UUID.randomUUID()).ownerId(ownerId)
            .title("Tokyo 2025").status(TripStatus.PLANNING).build();
        when(tripRepository.save(any())).thenReturn(expected);

        var result = useCase.execute(cmd);

        assertThat(result).isEqualTo(expected);
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void execute_setsStatusToPlanning() {
        when(tripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var result = useCase.execute(cmd);
        assertThat(result.getStatus()).isEqualTo(TripStatus.PLANNING);
    }

    @Test
    void execute_setsIsPublicFalse() {
        when(tripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var result = useCase.execute(cmd);
        assertThat(result.isPublic()).isFalse();
    }

    @Test
    void execute_assignsOwnerIdFromCommand() {
        when(tripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var result = useCase.execute(cmd);
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void execute_generatesRandomId() {
        when(tripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var r1 = useCase.execute(cmd);
        var r2 = useCase.execute(cmd);
        assertThat(r1.getId()).isNotEqualTo(r2.getId());
    }
}
