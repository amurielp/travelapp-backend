-- Rename amount_estimated → amount, drop amount_actual
-- Drop dependent views first so the column drop does not fail
DROP VIEW IF EXISTS v_payment_method_summary;
DROP VIEW IF EXISTS v_payment_method_report;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'expenses'
                 AND column_name = 'amount_estimated') THEN
        ALTER TABLE expenses RENAME COLUMN amount_estimated TO amount;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'expenses'
                 AND column_name = 'amount_actual') THEN
        ALTER TABLE expenses DROP COLUMN amount_actual;
    END IF;
END $$;

-- Update the v_payment_method_report view to use the renamed column
CREATE OR REPLACE VIEW v_payment_method_report AS
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
        ex.payment_method_id,
        ex.trip_id,
        ex.category             AS expense_type,
        ex.description,
        ex.amount,
        ex.currency,
        CASE WHEN ex.is_paid THEN 'CONFIRMED' ELSE 'PENDING' END AS purchase_status,
        ex.paid_at
    FROM expenses ex
    WHERE ex.payment_method_id IS NOT NULL
      AND ex.deleted_at IS NULL

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

CREATE OR REPLACE VIEW v_payment_method_summary AS
SELECT
    pm.id                   AS payment_method_id,
    pm.name                 AS payment_method_name,
    pm.type                 AS payment_method_type,
    pm.user_id,
    ae.currency,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'CONFIRMED') AS total_confirmed,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'RESERVED')  AS total_reserved,
    SUM(ae.amount) FILTER (WHERE ae.purchase_status = 'PENDING')   AS total_pending,
    SUM(ae.amount)          AS total_all
FROM v_payment_method_report ae
JOIN payment_methods pm ON pm.id = ae.payment_method_id
GROUP BY pm.id, pm.name, pm.type, pm.user_id, ae.currency;
