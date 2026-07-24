package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TripEntity;
import com.travelapp.persistence.entities.TripStatusEntity;
import com.travelapp.persistence.mappers.TripMapper;
import com.travelapp.trips.domain.*;
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
class TripRepositoryAdapterTest {

    @Mock TripJpaRepository jpa;
    @Mock TripMapper mapper;
    @InjectMocks TripRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();

    private Trip trip() {
        return Trip.builder().id(id).ownerId(ownerId).title("Trip")
            .status(TripStatus.PLANNING).startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(5)).baseCurrency("EUR")
            .isPublic(false).build();
    }

    private TripEntity entity() {
        return TripEntity.builder().id(id).ownerId(ownerId).title("Trip")
            .status(TripStatusEntity.PLANNING).startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(5)).baseCurrency("EUR")
            .isPublic(false).build();
    }

    @Test
    void save_mapsAndSaves() {
        var trip = trip();
        var entity = entity();
        when(mapper.toEntity(trip)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(trip);

        var result = adapter.save(trip);

        assertThat(result).isEqualTo(trip);
        verify(jpa).save(entity);
    }

    @Test
    void findById_found_returnsMappedDomain() {
        var entity = entity();
        var trip = trip();
        when(jpa.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(trip);

        var result = adapter.findById(id);

        assertThat(result).contains(trip);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByOwnerId_returnsMappedList() {
        var entity = entity();
        var trip = trip();
        when(jpa.findByOwnerIdOrderByStartDateDesc(ownerId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(trip);

        var result = adapter.findByOwnerId(ownerId);

        assertThat(result).containsExactly(trip);
    }

    @Test
    void findSharedWith_returnsEmptyList() {
        assertThat(adapter.findSharedWith(UUID.randomUUID())).isEmpty();
        verifyNoInteractions(jpa);
    }

    @Test
    void deleteById_callsJpa() {
        adapter.deleteById(id);
        verify(jpa).deleteById(id);
    }

    @Test
    void existsByPublicSlug_delegates() {
        when(jpa.existsByPublicSlug("my-slug")).thenReturn(true);
        assertThat(adapter.existsByPublicSlug("my-slug")).isTrue();
    }
}
