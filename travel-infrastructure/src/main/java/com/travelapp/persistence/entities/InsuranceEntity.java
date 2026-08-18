package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "insurances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InsuranceEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "event_id")
    private EventEntity event;

    private String provider;
    @Column(name = "policy_number")      private String policyNumber;
    @Column(name = "coverage_type")      private String coverageType;
    @Column(name = "coverage_amount_eur") private Double coverageAmountEur;
    @Column(name = "emergency_phone")    private String emergencyPhone;
    @Column(name = "deductible_amount")  private Double deductibleAmount;
    @Column(name = "purchase_status")    private String purchaseStatus;
    @Column(name = "price_amount")       private Double priceAmount;
    @Column(name = "price_currency")     private String priceCurrency;
    @Column(name = "purchased_at")       private OffsetDateTime purchasedAt;
    private String beneficiaries;
}
