-- Campos de pago programado y avisos en budget_items
ALTER TABLE budget_items
    ADD COLUMN IF NOT EXISTS payment_method_id     UUID REFERENCES payment_methods(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS scheduled_pay_at      TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS reminder_hours_before INTEGER,
    ADD COLUMN IF NOT EXISTS reminder_sent_at      TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_budget_items_scheduled_pay ON budget_items(scheduled_pay_at)
    WHERE scheduled_pay_at IS NOT NULL AND reminder_sent_at IS NULL;
