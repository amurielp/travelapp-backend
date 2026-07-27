-- V8 — Corrige tipo de latitud/longitud en accommodations: DECIMAL(10,7) → DOUBLE PRECISION
-- V5 ya lo hizo para events y wishlist_items; accommodations quedó pendiente.

ALTER TABLE accommodations
    ALTER COLUMN latitude  TYPE DOUBLE PRECISION USING latitude::DOUBLE PRECISION,
    ALTER COLUMN longitude TYPE DOUBLE PRECISION USING longitude::DOUBLE PRECISION;
