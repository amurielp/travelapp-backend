-- V12 — Campos adicionales en accommodations: número de confirmación, tipo de habitación y nº huéspedes

ALTER TABLE accommodations
    ADD COLUMN IF NOT EXISTS confirmation_number TEXT,
    ADD COLUMN IF NOT EXISTS room_type           TEXT,
    ADD COLUMN IF NOT EXISTS num_guests          INTEGER;
