-- V10 — Corrige flights.num_passengers: SMALLINT → INTEGER
-- Hibernate mapea Integer a integer(10); SMALLINT es int2 y falla la validación.

ALTER TABLE flights
    ALTER COLUMN num_passengers TYPE INTEGER USING num_passengers::INTEGER;
