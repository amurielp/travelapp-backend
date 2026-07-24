package com.travelapp.events.domain;

import com.travelapp.shared.domain.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AccommodationDetail {
    private String         name;
    private String         accommodationType;
    private String         address;
    private String         city;
    private String         country;
    private Double         latitude;
    private Double         longitude;
    private java.time.LocalDate checkInDate;
    private java.time.LocalDate checkOutDate;
    private java.time.LocalTime checkInTime;
    private java.time.LocalTime checkOutTime;
    private String         bookingRef;
    private String         bookingPlatform;
    private String         confirmationNumber;
    private String         roomType;
    private Integer        numGuests;
    private Boolean        includesBreakfast;

    // ── Campos de compra (nuevos) ─────────────────────────────
    private PurchaseStatus purchaseStatus;
    private UUID           paymentMethodId;
    private BigDecimal     pricePerNight;
    private BigDecimal     totalPrice;
    private String         priceCurrency;
    private OffsetDateTime freeCancellationUntil;
    private BigDecimal     cancellationPenalty;
    private OffsetDateTime purchasedAt;
    private String         notesInternal;

    public int totalNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public BigDecimal computedTotalPrice() {
        if (totalPrice != null) return totalPrice;
        if (pricePerNight != null) return pricePerNight.multiply(BigDecimal.valueOf(totalNights()));
        return BigDecimal.ZERO;
    }

    public boolean isFreeCancel() {
        return freeCancellationUntil != null && OffsetDateTime.now().isBefore(freeCancellationUntil);
    }
}
