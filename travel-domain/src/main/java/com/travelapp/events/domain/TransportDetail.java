package com.travelapp.events.domain;
import com.travelapp.shared.domain.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TransportDetail {
    private String         transportType;
    private String         provider;
    private String         originName;
    private String         originAddress;
    private Double         originLatitude;
    private Double         originLongitude;
    private String         destinationName;
    private String         destinationAddress;
    private Double         destinationLatitude;
    private Double         destinationLongitude;
    private String         bookingRef;
    private String         seatNumber;
    private String         vehicleDetails;
    private String         licensePlate;
    private String         pickupInstructions;
    private PurchaseStatus purchaseStatus;
    private UUID           paymentMethodId;
    private BigDecimal     priceAmount;
    private String         priceCurrency;
}
