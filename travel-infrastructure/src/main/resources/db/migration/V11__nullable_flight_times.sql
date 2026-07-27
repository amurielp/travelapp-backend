-- V11 — departure_at y arrival_at pasan a ser opcionales en flights
-- Un vuelo puede guardarse como borrador sin horarios confirmados.

ALTER TABLE flights
    ALTER COLUMN departure_at DROP NOT NULL,
    ALTER COLUMN arrival_at   DROP NOT NULL;
