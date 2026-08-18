package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEventUseCaseTest {

    @Mock EventRepository eventRepository;
    @InjectMocks UpdateEventUseCase useCase;

    private final UUID tripId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();

    private TravelEvent event() {
        return TravelEvent.builder()
            .id(eventId).tripId(tripId)
            .type(EventType.ACTIVITY).title("Museum")
            .startDatetime(OffsetDateTime.now()).allDay(false)
            .timezone(ZoneId.of("Europe/Madrid"))
            .status(EventStatus.CONFIRMED).source(EventSource.MANUAL)
            .build();
    }

    @Test
    void execute_found_updatesAndSaves() {
        var existing = event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new UpdateEventCommand(eventId, tripId, "Prado Museum",
            null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        var result = useCase.execute(cmd);

        assertThat(result.getTitle()).isEqualTo("Prado Museum");
        verify(eventRepository).save(any());
    }

    @Test
    void execute_notFound_throwsEventNotFoundException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        var cmd = new UpdateEventCommand(eventId, tripId, "X",
            null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(EventNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void execute_differentTripId_throwsEventNotFoundException() {
        var existing = event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));

        var wrongTripId = UUID.randomUUID();
        var cmd = new UpdateEventCommand(eventId, wrongTripId, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(EventNotFoundException.class);
    }
}
