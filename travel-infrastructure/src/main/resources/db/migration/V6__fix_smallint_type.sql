-- V6 — Corrige columnas SMALLINT que Hibernate espera como INTEGER
-- int/Integer en Java mapea a INTEGER; SMALLINT genera int2, que falla la validación.

ALTER TABLE payment_methods
    ALTER COLUMN sort_order TYPE INTEGER USING sort_order::INTEGER;

ALTER TABLE wishlist_items
    ALTER COLUMN priority TYPE INTEGER USING priority::INTEGER;
