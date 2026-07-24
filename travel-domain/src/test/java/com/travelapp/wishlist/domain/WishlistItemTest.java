package com.travelapp.wishlist.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class WishlistItemTest {

    private WishlistItem item() {
        return WishlistItem.builder()
            .id(UUID.randomUUID()).tripId(UUID.randomUUID())
            .name("La Boqueria").category("gastronomy")
            .destinationCity("Barcelona").priority(2)
            .estimatedCost(new BigDecimal("15")).build();
    }

    @Test
    void markConverted_setsEventId() {
        var item = item();
        var eventId = UUID.randomUUID();
        item.markConverted(eventId);
        assertThat(item.getConvertedToEventId()).isEqualTo(eventId);
    }

    @Test
    void isConverted_whenNotConverted_returnsFalse() {
        assertThat(item().isConverted()).isFalse();
    }

    @Test
    void isConverted_whenConverted_returnsTrue() {
        var item = item();
        item.markConverted(UUID.randomUUID());
        assertThat(item.isConverted()).isTrue();
    }
}
