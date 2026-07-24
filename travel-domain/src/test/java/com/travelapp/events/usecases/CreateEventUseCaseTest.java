package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventOverlapException;
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
class CreateEventUseCaseTest {

    @Mock EventRepository eventRepository;
    @InjectMocks CreateEventUseCase useCase;

    private final UUID tripId = UUID.randomUUID();
    private final ZoneId tz = ZoneId.of("Europe/Madrid");

    private OffsetDateTime dt(int hour) {
        return OffsetDateTime.of(2025, 6, 15, hour, 0, 0, 0, ZoneOffset.UTC);
    }

    private CreateEventCommand cmd(EventType type, int startHour, int endHour) {
        return new CreateEventCommand(
            tripId, null, type, "Event", null, null,
            dt(startHour), endHour >= 0 ? dt(endHour) : null,
            false, tz, EventSource.MANUAL,
            null, null, null, null, null, null, null
        );
    }

    private TravelEvent existingEvent(int startHour, int endHour, EventType type) {
        return TravelEvent.builder()
            .id(UUID.randomUUID()).tripId(tripId)
            .type(type).title("Existing")
            .startDatetime(dt(startHour)).endDatetime(dt(endHour))
            .allDay(false).timezone(tz)
            .status(EventStatus.CONFIRMED).source(EventSource.MANUAL)
            .build();
    }

    @Test
    void execute_noOverlap_savesEvent() {
        when(eventRepository.findByTripIdAndDateRange(any(), any(), any()))
            .thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd(EventType.ACTIVITY, 10, 12));

        assertThat(result.getStatus()).isEqualTo(EventStatus.CONFIRMED);
        assertThat(result.getSource()).isEqualTo(EventSource.MANUAL);
        verify(eventRepository).save(any());
    }

    @Test
    void execute_withOverlap_throwsEventOverlapException() {
        var overlapping = existingEvent(11, 13, EventType.ACTIVITY);
        when(eventRepository.findByTripIdAndDateRange(any(), any(), any()))
            .thenReturn(List.of(overlapping));

        assertThatThrownBy(() -> useCase.execute(cmd(EventType.ACTIVITY, 10, 12)))
            .isInstanceOf(EventOverlapException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void execute_accommodation_noOverlapCheck() {
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // ACCOMMODATION type skips overlap check — repository not called for date range
        var result = useCase.execute(cmd(EventType.ACCOMMODATION, 14, -1));

        assertThat(result.getType()).isEqualTo(EventType.ACCOMMODATION);
        verify(eventRepository, never()).findByTripIdAndDateRange(any(), any(), any());
    }

    @Test
    void execute_nullSource_defaultsToManual() {
        when(eventRepository.findByTripIdAndDateRange(any(), any(), any()))
            .thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd(EventType.ACTIVITY, 10, 12));

        assertThat(result.getSource()).isEqualTo(EventSource.MANUAL);
    }
}
