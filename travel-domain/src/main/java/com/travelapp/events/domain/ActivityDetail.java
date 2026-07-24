package com.travelapp.events.domain;
import com.travelapp.shared.domain.PurchaseStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ActivityDetail {
    private String         venueName;
    private String         address;
    private String         city;
    private Double         latitude;
    private Double         longitude;
    private String         category;
    private String         bookingRef;
    private String         ticketUrl;
    private Integer        numPeople;
    private String         externalPlaceId;
    private Double         rating;
    private String         websiteUrl;
    private String         phone;
    private PurchaseStatus purchaseStatus;
    private UUID           paymentMethodId;
    private BigDecimal     priceAmount;
    private String         priceCurrency;
}
