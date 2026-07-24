package com.travelapp.persistence.repositories;

import com.travelapp.events.domain.*;
import com.travelapp.persistence.entities.EventEntity;
import com.travelapp.persistence.mappers.EventMapper;
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
class EventRepositoryAdapterTest {

    @Mock EventJpaRepository jpa;
    @Mock EventMapper mapper;
    @InjectMocks EventRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();

    private EventEntity entity() {
        var e = new EventEntity();
        e.setId(id); e.setTripId(tripId);
        e.setType("ACTIVITY"); e.setTitle("Test");
        e.setStartDatetime(OffsetDateTime.now());
        e.setTimezone("Europe/Madrid");
        e.setStatus("CONFIRMED"); e.setSource("MANUAL");
        return e;
    }

    private TravelEvent domain() {
        return TravelEvent.builder()
            .id(id).tripId(tripId).type(EventType.ACTIVITY).title("Test")
            .startDatetime(OffsetDateTime.now()).timezone(ZoneId.of("Europe/Madrid"))
            .status(EventStatus.CONFIRMED).source(EventSource.MANUAL).allDay(false)
            .build();
    }

    @Test
    void save_mapsAndSaves() {
        var d = domain();
        var e = entity();
        when(mapper.toEntity(d)).thenReturn(e);
        when(jpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(d);

        var result = adapter.save(d);
        assertThat(result).isEqualTo(d);
    }

    @Test
    void findById_found_returnsDomain() {
        var e = entity();
        var d = domain();
        when(jpa.findById(id)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(d);

        assertThat(adapter.findById(id)).contains(d);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByTripId_returnsMappedList() {
        var e = entity();
        var d = domain();
        when(jpa.findByTripIdOrderByStartDatetimeAsc(tripId)).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(d);

        assertThat(adapter.findByTripId(tripId)).containsExactly(d);
    }

    @Test
    void findByTripIdAndDateRange_queriesWithUTCRange() {
        var date = LocalDate.of(2025, 6, 15);
        var e = entity();
        var d = domain();
        when(jpa.findByTripIdAndDateRange(eq(tripId), any(), any())).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(d);

        var result = adapter.findByTripIdAndDateRange(tripId, date, date);
        assertThat(result).containsExactly(d);
        verify(jpa).findByTripIdAndDateRange(eq(tripId), any(OffsetDateTime.class), any(OffsetDateTime.class));
    }

    @Test
    void deleteById_callsJpa() {
        adapter.deleteById(id);
        verify(jpa).deleteById(id);
    }
}
