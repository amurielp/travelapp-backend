package com.travelapp.persistence.entities;

import com.travelapp.shared.domain.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "flights")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FlightEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventEntity event;

    private String airline;
    @Column(name = "flight_number")        private String flightNumber;
    @Column(name = "origin_city")          private String originCity;
    @Column(name = "origin_iata")          private String originIata;
    @Column(name = "origin_terminal")      private String originTerminal;
    @Column(name = "destination_city")     private String destinationCity;
    @Column(name = "destination_iata")     private String destinationIata;
    @Column(name = "destination_terminal") private String destinationTerminal;
    private String seat;
    @Column(name = "cabin_class")          private String cabinClass;
    @Column(name = "booking_ref")          private String bookingRef;
    @Column(name = "baggage_allowance")    private String baggageAllowance;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "purchase_status", nullable = false, columnDefinition = "purchase_status")
    private PurchaseStatus purchaseStatus = PurchaseStatus.DRAFT;

    @Column(name = "payment_method_id")    private UUID paymentMethodId;
    @Column(name = "price_amount")         private BigDecimal priceAmount;
    @Column(name = "price_currency")       private String priceCurrency;
    @Column(name = "price_per_person")     private Boolean pricePerPerson;
    @Column(name = "num_passengers")       private Integer numPassengers;
    @Column(name = "cancellation_deadline") private OffsetDateTime cancellationDeadline;
    @Column(name = "cancellation_penalty")  private BigDecimal cancellationPenalty;
    @Column(name = "purchased_at")         private OffsetDateTime purchasedAt;
    @Column(name = "notes_internal")       private String notesInternal;
}
