package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "esims")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EsimEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "event_id")
    private EventEntity event;

    private String provider;
    @Column(name = "data_allowance_gb")   private Double dataAllowanceGb;
    @Column(name = "coverage_region")     private String coverageRegion;
    @Column(name = "activation_code")     private String activationCode;
    @Column(name = "activation_deadline") private LocalDate activationDeadline;
    @Column(name = "purchase_status")     private String purchaseStatus;
    @Column(name = "price_amount")        private Double priceAmount;
    @Column(name = "price_currency")      private String priceCurrency;
    @Column(name = "purchase_platform")   private String purchasePlatform;
    @Column(name = "purchased_at")        private OffsetDateTime purchasedAt;
}
