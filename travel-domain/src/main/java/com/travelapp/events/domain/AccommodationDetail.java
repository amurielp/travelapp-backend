package com.travelapp.events.domain;

import com.travelapp.shared.domain.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AccommodationDetail {
    private String         name;
    private String         accommodationType;
    private String         address;
    private String         city;
    private String         country;
    private String         bookingRef;
    private String         bookingPlatform;
    private String         confirmationNumber;
    private String         roomType;
    private Integer        numGuests;
    private Boolean        includesBreakfast;

    private PurchaseStatus purchaseStatus;
    private UUID           paymentMethodId;
    private BigDecimal     pricePerNight;
    private BigDecimal     totalPrice;
    private String         priceCurrency;
    private OffsetDateTime freeCancellationUntil;
    private BigDecimal     cancellationPenalty;
    private OffsetDateTime purchasedAt;
    private String         notesInternal;

    public boolean isFreeCancel() {
        return freeCancellationUntil != null && OffsetDateTime.now().isBefore(freeCancellationUntil);
    }
}
