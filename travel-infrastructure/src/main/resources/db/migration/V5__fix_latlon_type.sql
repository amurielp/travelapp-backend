-- V5 — Corrige tipo de latitud/longitud: DECIMAL(10,7) → DOUBLE PRECISION
-- Hibernate mapea Double a float(53) (DOUBLE PRECISION); DECIMAL genera NUMERIC, que falla la validación.

ALTER TABLE events
    ALTER COLUMN latitude  TYPE DOUBLE PRECISION USING latitude::DOUBLE PRECISION,
    ALTER COLUMN longitude TYPE DOUBLE PRECISION USING longitude::DOUBLE PRECISION;

ALTER TABLE wishlist_items
    ALTER COLUMN latitude  TYPE DOUBLE PRECISION USING latitude::DOUBLE PRECISION,
    ALTER COLUMN longitude TYPE DOUBLE PRECISION USING longitude::DOUBLE PRECISION;
