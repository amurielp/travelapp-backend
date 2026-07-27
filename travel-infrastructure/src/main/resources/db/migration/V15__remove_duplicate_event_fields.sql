-- V15: Remove fields from sub-event tables that duplicate data already stored in the events table.
--
-- FLIGHT:        departure_at  = events.start_datetime
--                arrival_at    = events.end_datetime
-- ACCOMMODATION: check_in/out dates & times = events.start_datetime / end_datetime
--                latitude/longitude         = events.latitude / longitude
-- ACTIVITY:      latitude/longitude         = events.latitude / longitude
--
-- Views that depend on total_nights are recreated using date arithmetic on events.

-- ── Drop dependent views first ────────────────────────────────
DROP VIEW IF EXISTS v_payment_method_summary;
DROP VIEW IF EXISTS v_payment_method_report;

-- ── flights ───────────────────────────────────────────────────
ALTER TABLE flights
    DROP COLUMN IF EXISTS departure_at,
    DROP COLUMN IF EXISTS arrival_at;

-- ── accommodations ────────────────────────────────────────────
ALTER TABLE accommodations
    DROP COLUMN IF EXISTS total_nights,
    DROP COLUMN IF EXISTS check_in_date,
    DROP COLUMN IF EXISTS check_out_date,
    DROP COLUMN IF EXISTS check_in_time,
    DROP COLUMN IF EXISTS check_out_time,
    DROP COLUMN IF EXISTS latitude,
    DROP COLUMN IF EXISTS longitude;

-- ── activities ────────────────────────────────────────────────
ALTER TABLE activities
    DROP COLUMN IF EXISTS latitude,
    DROP COLUMN IF EXISTS longitude;

-- ── Recreate views using event dates for night calculation ────
CREATE VIEW v_payment_method_report AS
WITH all_expenses AS (

    SELECT
        f.payment_method_id,
        e.trip_id,
        'flight'                AS expense_type,
        e.title                 AS description,
        f.price_amount          AS amount,
        f.price_currency        AS currency,
        f.purchase_status::TEXT AS purchase_status,
        f.purchased_at          AS paid_at
    FROM flights f
    JOIN events e ON e.id = f.event_id
    WHERE f.payment_method_id IS NOT NULL
      AND f.purchase_status IN ('PENDING','RESERVED','CONFIRMED')

    UNION ALL

    SELECT
        a.payment_method_id,
        e.trip_id,
        'accommodation'         AS expense_type,
        a.name                  AS description,
        COALESCE(
            a.total_price,
            a.price_per_night * GREATEST(1, (e.end_datetime::date - e.start_datetime::date))
        )                       AS amount,
        a.price_currency        AS currency,
        a.purchase_status::TEXT AS purchase_status,
        a.purchased_at          AS paid_at
    FROM accommodations a
    JOIN events e ON e.id = a.event_id
    WHERE a.payment_method_id IS NOT NULL
      AND a.purchase_status IN ('PENDING','RESERVED','CONFIRMED')

    UNION ALL

    SELECT
        bi.payment_method_id,
        b.trip_id,
        bi.category             AS expense_type,
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
    pm.id                   AS payment_method_id,
    pm.name                 AS payment_method_name,
    pm.type                 AS payment_method_type,
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

CREATE VIEW v_payment_method_summary AS
SELECT
    pm.id                   AS payment_method_id,
    pm.name                 AS payment_method_name,
    pm.type,
    pm.user_id,
    ae.currency,
    COUNT(*)                AS num_expenses,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'CONFIRMED') AS total_confirmed,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'RESERVED')  AS total_reserved,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'PENDING')   AS total_pending,
    SUM(ae.amount)          AS total_all
FROM v_payment_method_report ae
JOIN payment_methods pm ON pm.id = ae.payment_method_id
GROUP BY pm.id, pm.name, pm.type, pm.user_id, ae.currency;
