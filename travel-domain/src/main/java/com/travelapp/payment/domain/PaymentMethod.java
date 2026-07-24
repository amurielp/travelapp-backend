package com.travelapp.payment.domain;

import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Medio de pago del usuario.
 * Solo almacena nombre y tipo — sin datos financieros sensibles.
 * Ejemplos: "Visa *4242", "PayPal", "Efectivo", "Transferencia empresa"
 */
@Getter @Builder @AllArgsConstructor
public class PaymentMethod extends AggregateRoot<UUID> {
    private final UUID          id;
    private final UUID          userId;
    private String              name;
    private PaymentMethodType   type;
    private boolean             isActive;
    private String              notes;
    private int                 sortOrder;
    private final OffsetDateTime createdAt;
    private OffsetDateTime      updatedAt;

    public void rename(String name) { this.name = name; }
    public void deactivate()        { this.isActive = false; }
    public void activate()          { this.isActive = true; }
}
