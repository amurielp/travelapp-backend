-- Add trip_id column populated from budgets join
ALTER TABLE budget_items ADD COLUMN IF NOT EXISTS trip_id UUID;

UPDATE budget_items bi
SET trip_id = b.trip_id
FROM budgets b
WHERE bi.budget_id = b.id;

ALTER TABLE budget_items ALTER COLUMN trip_id SET NOT NULL;

-- Drop dependent views before renaming columns/tables
DROP VIEW IF EXISTS v_payment_method_summary;
DROP VIEW IF EXISTS v_payment_method_report;

-- Rename table and drop old budget_id column (CASCADE not needed since views are dropped above)
ALTER TABLE budget_items RENAME TO expenses;
ALTER TABLE expenses DROP COLUMN IF EXISTS budget_id;

-- Drop old indexes and create new ones
DROP INDEX IF EXISTS idx_budget_items_b;
DROP INDEX IF EXISTS idx_budget_items_scheduled_pay;
CREATE INDEX idx_expenses_trip ON expenses(trip_id);
CREATE INDEX idx_expenses_scheduled_pay ON expenses(scheduled_pay_at);

-- Drop the budgets table (no longer needed)
DROP TABLE IF EXISTS budgets;

-- Recreate views using the new expenses table with trip_id directly
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
        ex.payment_method_id,
        ex.trip_id,
        ex.category             AS expense_type,
        ex.description,
        COALESCE(ex.amount_actual, ex.amount_estimated) AS amount,
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

CREATE VIEW v_payment_method_summary AS
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
