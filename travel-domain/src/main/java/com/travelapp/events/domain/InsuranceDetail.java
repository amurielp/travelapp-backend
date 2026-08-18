package com.travelapp.events.domain;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class InsuranceDetail {
    private String         provider;           // Allianz, AXA, Mapfre…
    private String         policyNumber;
    private String         coverageType;       // medical / cancellation / comprehensive / basic
    private Double         coverageAmountEur;
    private String         emergencyPhone;
    private Double         deductibleAmount;
    private String         purchaseStatus;
    private Double         priceAmount;
    private String         priceCurrency;
    private OffsetDateTime purchasedAt;
    private String         beneficiaries;
}
