package com.travelapp.events.domain;

import com.travelapp.shared.domain.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FlightDetail {
    private String         airline;
    private String         flightNumber;
    private String         originCity;
    private String         originIata;
    private String         originTerminal;
    private String         destinationCity;
    private String         destinationIata;
    private String         destinationTerminal;
    private OffsetDateTime departureAt;
    private OffsetDateTime arrivalAt;
    private String         seat;
    private String         cabinClass;
    private String         bookingRef;
    private String         baggageAllowance;
    private boolean        isConnection;

    // ── Campos de compra (nuevos) ─────────────────────────────
    private PurchaseStatus purchaseStatus;     // DRAFT por defecto
    private UUID           paymentMethodId;
    private BigDecimal     priceAmount;
    private String         priceCurrency;
    private Boolean        pricePerPerson;
    private Integer        numPassengers;
    private OffsetDateTime cancellationDeadline;
    private BigDecimal     cancellationPenalty;
    private OffsetDateTime purchasedAt;
    private String         notesInternal;

    public BigDecimal totalPrice() {
        if (priceAmount == null) return BigDecimal.ZERO;
        if (Boolean.TRUE.equals(pricePerPerson) && numPassengers != null) {
            return priceAmount.multiply(BigDecimal.valueOf(numPassengers));
        }
        return priceAmount;
    }

    public boolean canBeCancelled() {
        return purchaseStatus != null && purchaseStatus.isCancellable();
    }

    public boolean isFreeCancel() {
        return cancellationDeadline != null && OffsetDateTime.now().isBefore(cancellationDeadline);
    }
}
