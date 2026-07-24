package com.travelapp.wishlist.domain;
import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class WishlistItem extends AggregateRoot<UUID> {
    private final UUID   id;
    private final UUID   tripId;
    private String       name;
    private String       category;
    private String       destinationCity;
    private Double       latitude, longitude;
    private String       externalPlaceId;
    private String       source;
    private int          priority;
    private String       notes;
    private BigDecimal   estimatedCost;
    private String       websiteUrl;
    private UUID         convertedToEventId;

    public void markConverted(UUID eventId) { this.convertedToEventId = eventId; }
    public boolean isConverted() { return convertedToEventId != null; }
}
