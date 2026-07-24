package com.travelapp.shared.domain;

/**
 * Estado de compra aplicable a vuelos y alojamientos.
 *
 * Flujo normal:
 *   DRAFT → PENDING → RESERVED → CONFIRMED
 *
 * Cancelación (desde cualquier estado):
 *   cualquiera → CANCELLED → REFUNDED
 *
 * DRAFT:     Opción guardada sin reservar. Permite tener varios vuelos
 *            alternativos y elegir cuál confirmar más adelante.
 * PENDING:   Reservado (precio bloqueado) pero sin pagar aún.
 * RESERVED:  Pagado con posibilidad de reembolso hasta cancellation_deadline.
 * CONFIRMED: Pagado y sin opción de cambio ni reembolso.
 * CANCELLED: Cancelado — puede tener penalización económica.
 * REFUNDED:  Cancelado y reembolsado total o parcialmente.
 */
public enum PurchaseStatus {
    DRAFT, PENDING, RESERVED, CONFIRMED, CANCELLED, REFUNDED;

    public boolean isPaid() {
        return this == RESERVED || this == CONFIRMED;
    }

    public boolean isActive() {
        return this == DRAFT || this == PENDING || this == RESERVED || this == CONFIRMED;
    }

    public boolean isCancellable() {
        return this == DRAFT || this == PENDING || this == RESERVED;
    }
}
