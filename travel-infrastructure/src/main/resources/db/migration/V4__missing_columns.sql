-- ============================================================
-- V4 — Columnas faltantes detectadas al implementar los adaptadores JPA
-- ============================================================

-- wishlist_items: notas, coste estimado y URL del sitio web
ALTER TABLE wishlist_items
    ADD COLUMN IF NOT EXISTS notes          TEXT,
    ADD COLUMN IF NOT EXISTS estimated_cost DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS website_url    TEXT;

-- budget_items: notas del item
ALTER TABLE budget_items
    ADD COLUMN IF NOT EXISTS notes TEXT;
