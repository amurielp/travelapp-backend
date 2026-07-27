package com.travelapp.persistence.entities;

import com.travelapp.shared.domain.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "accommodations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccommodationEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventEntity event;

    @Column(nullable = false) private String name;
    @Column(name = "accommodation_type")   private String accommodationType;
    private String address;
    private String city;
    private String country;
    @Column(name = "booking_ref")          private String bookingRef;
    @Column(name = "booking_platform")     private String bookingPlatform;
    @Column(name = "confirmation_number")  private String confirmationNumber;
    @Column(name = "room_type")            private String roomType;
    @Column(name = "num_guests")           private Integer numGuests;
    @Column(name = "includes_breakfast")   private Boolean includesBreakfast;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "purchase_status", nullable = false, columnDefinition = "purchase_status")
    private PurchaseStatus purchaseStatus = PurchaseStatus.DRAFT;

    @Column(name = "payment_method_id")        private UUID paymentMethodId;
    @Column(name = "price_per_night")          private BigDecimal pricePerNight;
    @Column(name = "total_price")              private BigDecimal totalPrice;
    @Column(name = "price_currency")           private String priceCurrency;
    @Column(name = "free_cancellation_until")  private OffsetDateTime freeCancellationUntil;
    @Column(name = "cancellation_penalty")     private BigDecimal cancellationPenalty;
    @Column(name = "purchased_at")             private OffsetDateTime purchasedAt;
    @Column(name = "notes_internal")           private String notesInternal;
}
