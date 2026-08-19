package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

// PENDIENTE: funcionalidad de viajes compartidos no confirmada en scope del MVP móvil.
// Requiere decisión sobre multi-viajero y migración V23 (tabla trip_members).
public record TripInviteResponse(
    String        token,
    UUID          tripId,
    String        tripTitle,
    String        invitedBy,
    OffsetDateTime expiresAt
) {}
