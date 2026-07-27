-- V9 — Corrige tipo de rating en activities: DECIMAL(3,1) → DOUBLE PRECISION
-- Hibernate mapea Double a float(53); DECIMAL genera NUMERIC y falla la validación.

ALTER TABLE activities
    ALTER COLUMN rating TYPE DOUBLE PRECISION USING rating::DOUBLE PRECISION;
