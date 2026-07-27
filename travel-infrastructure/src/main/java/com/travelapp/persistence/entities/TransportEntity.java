package com.travelapp.persistence.entities;

import com.travelapp.shared.domain.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "transports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventEntity event;

    @Column(name = "transport_type")         private String transportType;
    private String provider;
    @Column(name = "origin_name")            private String originName;
    @Column(name = "origin_address")         private String originAddress;
    @Column(name = "origin_latitude")        private Double originLatitude;
    @Column(name = "origin_longitude")       private Double originLongitude;
    @Column(name = "destination_name")       private String destinationName;
    @Column(name = "destination_address")    private String destinationAddress;
    @Column(name = "destination_latitude")   private Double destinationLatitude;
    @Column(name = "destination_longitude")  private Double destinationLongitude;
    @Column(name = "booking_ref")            private String bookingRef;
    @Column(name = "seat_number")            private String seatNumber;
    @Column(name = "vehicle_details")        private String vehicleDetails;
    @Column(name = "license_plate")          private String licensePlate;
    @Column(name = "pickup_instructions")    private String pickupInstructions;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "purchase_status", nullable = false, columnDefinition = "purchase_status")
    private PurchaseStatus purchaseStatus = PurchaseStatus.DRAFT;

    @Column(name = "payment_method_id") private UUID paymentMethodId;
    @Column(name = "price_amount")      private BigDecimal priceAmount;
    @Column(name = "price_currency")    private String priceCurrency;

    @Column(name = "created_at", updatable = false) private OffsetDateTime createdAt;
    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(); }
}
