package com.travelapp.persistence.entities;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "wishlist_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WishlistItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "trip_id", nullable = false)         private UUID tripId;
    @Column(nullable = false)                           private String name;
    private String category;
    @Column(name = "destination_city")                  private String destinationCity;
    private Double latitude, longitude;
    @Column(name = "external_place_id")                 private String externalPlaceId;
    private String source;
    private int priority;
    private String notes;
    @Column(name = "estimated_cost")                    private BigDecimal estimatedCost;
    @Column(name = "website_url")                       private String websiteUrl;
    @Column(name = "converted_to_event_id")             private UUID convertedToEventId;
    @Column(updatable = false)                          private OffsetDateTime createdAt;
    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(); }
}
