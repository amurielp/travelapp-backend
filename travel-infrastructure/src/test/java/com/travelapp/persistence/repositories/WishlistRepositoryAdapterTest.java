package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.WishlistItemEntity;
import com.travelapp.persistence.mappers.WishlistMapper;
import com.travelapp.wishlist.domain.WishlistItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistRepositoryAdapterTest {

    @Mock WishlistJpaRepository jpa;
    @Mock WishlistMapper mapper;
    @InjectMocks WishlistRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();

    private WishlistItem item() {
        return WishlistItem.builder().id(id).tripId(tripId)
            .name("La Boqueria").category("gastronomy")
            .destinationCity("Barcelona").priority(2)
            .estimatedCost(new BigDecimal("15")).build();
    }

    private WishlistItemEntity entity() {
        var e = new WishlistItemEntity();
        e.setId(id); e.setTripId(tripId); e.setName("La Boqueria");
        return e;
    }

    @Test
    void save_mapsAndSaves() {
        var w = item();
        var e = entity();
        when(mapper.toEntity(w)).thenReturn(e);
        when(jpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(w);
        assertThat(adapter.save(w)).isEqualTo(w);
    }

    @Test
    void findById_found() {
        var e = entity();
        var w = item();
        when(jpa.findById(id)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(w);
        assertThat(adapter.findById(id)).contains(w);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByTripId_returnsList() {
        var e = entity();
        var w = item();
        when(jpa.findByTripIdOrderByPriorityAsc(tripId)).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(w);
        assertThat(adapter.findByTripId(tripId)).containsExactly(w);
    }

    @Test
    void findByTripIdAndCity_delegates() {
        var e = entity();
        var w = item();
        when(jpa.findByTripIdAndDestinationCityOrderByPriorityAsc(tripId, "Barcelona"))
            .thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(w);
        assertThat(adapter.findByTripIdAndCity(tripId, "Barcelona")).containsExactly(w);
    }

    @Test
    void deleteById_callsJpa() {
        adapter.deleteById(id);
        verify(jpa).deleteById(id);
    }
}
