package com.travelapp.persistence.entities;

import com.travelapp.shared.domain.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventEntity event;

    @Column(name = "venue_name")       private String venueName;
    private String address;
    private String city;
    private String category;
    @Column(name = "booking_ref")      private String bookingRef;
    @Column(name = "ticket_url")       private String ticketUrl;
    @Column(name = "num_people")       private Integer numPeople;
    @Column(name = "external_place_id") private String externalPlaceId;
    private Double rating;
    @Column(name = "website_url")      private String websiteUrl;
    private String phone;

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
