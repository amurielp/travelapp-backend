package com.travelapp.events.domain;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EsimDetail {
    private String         provider;           // Airalo, Holafly, Nomad…
    private Double         dataAllowanceGb;
    private String         coverageRegion;     // Europe, World, ES+FR+IT…
    private String         activationCode;     // QR text / LPA string
    private LocalDate      activationDeadline;
    private String         purchaseStatus;     // DRAFT/CONFIRMED/etc.
    private Double         priceAmount;
    private String         priceCurrency;
    private String         purchasePlatform;
    private OffsetDateTime purchasedAt;
}
