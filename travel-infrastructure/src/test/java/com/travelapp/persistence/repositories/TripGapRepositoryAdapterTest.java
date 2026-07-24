package com.travelapp.persistence.repositories;

import com.travelapp.gaps.domain.*;
import com.travelapp.persistence.entities.TripGapEntity;
import com.travelapp.persistence.mappers.TripGapMapper;
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
class TripGapRepositoryAdapterTest {

    @Mock TripGapJpaRepository jpa;
    @Mock TripGapMapper mapper;
    @InjectMocks TripGapRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();

    private TripGap gap() {
        return TripGap.builder()
            .id(id).tripId(tripId)
            .gapType(GapType.NO_TRANSPORT_BETWEEN_DESTINATIONS)
            .severity(GapSeverity.WARNING)
            .affectedFrom(LocalDate.now())
            .status(GapStatus.OPEN)
            .detectedAt(OffsetDateTime.now())
            .build();
    }

    private TripGapEntity entity() {
        var e = new TripGapEntity();
        e.setId(id); e.setTripId(tripId);
        e.setGapType("NO_TRANSPORT_BETWEEN_DESTINATIONS");
        e.setSeverity("WARNING"); e.setStatus("OPEN");
        e.setAffectedFrom(LocalDate.now());
        return e;
    }

    @Test
    void save_mapsAndSaves() {
        var g = gap();
        var e = entity();
        when(mapper.toEntity(g)).thenReturn(e);
        when(jpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(g);
        assertThat(adapter.save(g)).isEqualTo(g);
    }

    @Test
    void findOpenByTripId_queriesWithOpenStatus() {
        var e = entity();
        var g = gap();
        when(jpa.findByTripIdAndStatusOrderByAffectedFromAsc(tripId, "OPEN"))
            .thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(g);
        assertThat(adapter.findOpenByTripId(tripId)).containsExactly(g);
    }

    @Test
    void findAllByTripId_returnsAll() {
        var e = entity();
        var g = gap();
        when(jpa.findByTripIdOrderByAffectedFromAsc(tripId)).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(g);
        assertThat(adapter.findAllByTripId(tripId)).containsExactly(g);
    }

    @Test
    void findById_found() {
        var e = entity();
        var g = gap();
        when(jpa.findById(id)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(g);
        assertThat(adapter.findById(id)).contains(g);
    }

    @Test
    void deleteByTripId_callsJpa() {
        adapter.deleteByTripId(tripId);
        verify(jpa).deleteByTripId(tripId);
    }

    @Test
    void saveAll_mapsAndBulkSaves() {
        var g = gap();
        var e = entity();
        when(mapper.toEntity(g)).thenReturn(e);
        when(jpa.saveAll(any())).thenReturn(List.of(e));
        adapter.saveAll(List.of(g));
        verify(jpa).saveAll(argThat(l -> ((List<?>) l).size() == 1));
    }
}
