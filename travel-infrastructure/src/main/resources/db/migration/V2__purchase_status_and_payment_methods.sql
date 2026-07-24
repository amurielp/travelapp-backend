-- ============================================================
-- V2 — Estados de compra + medios de pago
-- ============================================================

-- ── PAYMENT_METHODS — solo nombres, sin datos sensibles ──────
CREATE TABLE payment_methods (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        TEXT NOT NULL,                        -- "Visa *4242", "PayPal", "Efectivo"
    type        TEXT NOT NULL DEFAULT 'other',         -- 'card' | 'transfer' | 'cash' | 'crypto' | 'other'
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    notes       TEXT,                                 -- "tarjeta empresa", "miles Iberia"...
    sort_order  SMALLINT DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT payment_method_type_check
        CHECK (type IN ('card','transfer','cash','crypto','other'))
);

CREATE INDEX idx_payment_methods_user ON payment_methods(user_id);

-- ── PURCHASE STATUS — tipo enumerado ─────────────────────────
-- DRAFT:     opción guardada sin reservar — para comparar alternativas
-- PENDING:   reservado, precio bloqueado, pendiente de pago
-- RESERVED:  pagado con posibilidad de reembolso (cancellable)
-- CONFIRMED: pagado y sin posibilidad de cambio
-- CANCELLED: cancelado (puede tener penalización)
-- REFUNDED:  cancelado y reembolsado
CREATE TYPE purchase_status AS ENUM (
    'DRAFT', 'PENDING', 'RESERVED', 'CONFIRMED', 'CANCELLED', 'REFUNDED'
);

-- ── ALTER FLIGHTS — añadir campos de compra ──────────────────
ALTER TABLE flights
    ADD COLUMN purchase_status       purchase_status NOT NULL DEFAULT 'DRAFT',
    ADD COLUMN payment_method_id     UUID REFERENCES payment_methods(id) ON DELETE SET NULL,
    ADD COLUMN price_amount          DECIMAL(12,2),
    ADD COLUMN price_currency        TEXT DEFAULT 'EUR',
    ADD COLUMN price_per_person      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN num_passengers        SMALLINT DEFAULT 1,
    ADD COLUMN cancellation_deadline TIMESTAMPTZ,          -- hasta cuándo se puede cancelar gratis
    ADD COLUMN cancellation_penalty  DECIMAL(12,2),        -- penalización si se cancela tarde
    ADD COLUMN purchased_at          TIMESTAMPTZ,          -- cuándo se compró / reservó
    ADD COLUMN notes_internal        TEXT;                  -- notas privadas (número de confirmación, etc.)

-- ── ALTER ACCOMMODATIONS — añadir campos de compra ───────────
ALTER TABLE accommodations
    ADD COLUMN purchase_status       purchase_status NOT NULL DEFAULT 'DRAFT',
    ADD COLUMN payment_method_id     UUID REFERENCES payment_methods(id) ON DELETE SET NULL,
    ADD COLUMN price_per_night       DECIMAL(12,2),
    ADD COLUMN total_price           DECIMAL(12,2),        -- calculado o introducido manualmente
    ADD COLUMN price_currency        TEXT DEFAULT 'EUR',
    ADD COLUMN free_cancellation_until TIMESTAMPTZ,        -- deadline de cancelación gratuita
    ADD COLUMN cancellation_penalty  DECIMAL(12,2),
    ADD COLUMN purchased_at          TIMESTAMPTZ,
    ADD COLUMN notes_internal        TEXT;

-- ── ALTER BUDGET_ITEMS — vincular a medio de pago ────────────
ALTER TABLE budget_items
    ADD COLUMN payment_method_id UUID REFERENCES payment_methods(id) ON DELETE SET NULL;

-- ── ÍNDICES ───────────────────────────────────────────────────
CREATE INDEX idx_flights_purchase_status       ON flights(purchase_status);
CREATE INDEX idx_flights_payment_method        ON flights(payment_method_id);
CREATE INDEX idx_accommodations_purchase_status ON accommodations(purchase_status);
CREATE INDEX idx_accommodations_payment_method  ON accommodations(payment_method_id);
CREATE INDEX idx_budget_items_payment_method    ON budget_items(payment_method_id);

-- ── VISTA: reporte por medio de pago ─────────────────────────
-- Agrupa todos los gastos (vuelos, hoteles, budget_items) por medio de pago
-- Con detalle de estado para saber qué está pendiente vs pagado

CREATE VIEW v_payment_method_report AS
WITH all_expenses AS (

    -- Vuelos confirmados/reservados
    SELECT
        f.payment_method_id,
        e.trip_id,
        'flight'                        AS expense_type,
        e.title                         AS description,
        f.price_amount                  AS amount,
        f.price_currency                AS currency,
        f.purchase_status::TEXT         AS purchase_status,
        f.purchased_at                  AS paid_at
    FROM flights f
    JOIN events e ON e.id = f.event_id
    WHERE f.payment_method_id IS NOT NULL
      AND f.purchase_status IN ('PENDING','RESERVED','CONFIRMED')

    UNION ALL

    -- Alojamientos confirmados/reservados
    SELECT
        a.payment_method_id,
        e.trip_id,
        'accommodation'                 AS expense_type,
        a.name                          AS description,
        COALESCE(a.total_price, a.price_per_night * a.total_nights) AS amount,
        a.price_currency                AS currency,
        a.purchase_status::TEXT         AS purchase_status,
        a.purchased_at                  AS paid_at
    FROM accommodations a
    JOIN events e ON e.id = a.event_id
    WHERE a.payment_method_id IS NOT NULL
      AND a.purchase_status IN ('PENDING','RESERVED','CONFIRMED')

    UNION ALL

    -- Gastos del presupuesto (actividades, comida, compras...)
    SELECT
        bi.payment_method_id,
        b.trip_id,
        bi.category                     AS expense_type,
        bi.description,
        COALESCE(bi.amount_actual, bi.amount_estimated) AS amount,
        bi.currency,
        CASE WHEN bi.is_paid THEN 'CONFIRMED' ELSE 'PENDING' END AS purchase_status,
        bi.paid_at
    FROM budget_items bi
    JOIN budgets b ON b.id = bi.budget_id
    WHERE bi.payment_method_id IS NOT NULL
)

SELECT
    pm.id                               AS payment_method_id,
    pm.name                             AS payment_method_name,
    pm.type                             AS payment_method_type,
    pm.user_id,
    ae.trip_id,
    ae.expense_type,
    ae.description,
    ae.amount,
    ae.currency,
    ae.purchase_status,
    ae.paid_at
FROM all_expenses ae
JOIN payment_methods pm ON pm.id = ae.payment_method_id;

-- ── VISTA: resumen por medio de pago (para el report de cabecera)
CREATE VIEW v_payment_method_summary AS
SELECT
    pm.id                               AS payment_method_id,
    pm.name                             AS payment_method_name,
    pm.type,
    pm.user_id,
    ae.currency,
    COUNT(*)                            AS num_expenses,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'CONFIRMED') AS total_confirmed,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'RESERVED')  AS total_reserved,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'PENDING')   AS total_pending,
    SUM(ae.amount)                      AS total_all
FROM v_payment_method_report ae
JOIN payment_methods pm ON pm.id = ae.payment_method_id
GROUP BY pm.id, pm.name, pm.type, pm.user_id, ae.currency;

