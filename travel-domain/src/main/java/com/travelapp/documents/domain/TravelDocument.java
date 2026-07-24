package com.travelapp.documents.domain;

import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class TravelDocument extends AggregateRoot<UUID> {
    private final UUID     id;
    private final UUID     tripId;
    private final UUID     uploadedBy;
    private String         documentTypeId;   // FK a document_types
    private String         displayName;      // "Billete IB3456", "Pasaporte María"
    private String         fileName;
    private String         fileUrl;
    private long           fileSizeBytes;
    private String         fileType;
    private ParseStatus    parseStatus;
    private String         rawExtracted;
    private Object         parsedData;       // JSONB
    private String         parseError;
    private LocalDate      validFrom;
    private LocalDate      validUntil;
    private String         notes;
    private final OffsetDateTime uploadedAt;

    public boolean requiresAiParse() {
        return java.util.Set.of("flight_ticket","hotel_voucher","car_rental","train_ticket","bus_ticket")
            .contains(documentTypeId);
    }

    public boolean isExpired() {
        return validUntil != null && validUntil.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        return validUntil != null && validUntil.isBefore(LocalDate.now().plusDays(30));
    }
}
