-- V19: Tablas de planes de suscripción y suscripciones de usuario
-- Soporta monetización freemium con compras en App Store (Apple) y Google Play, y Stripe para web.

CREATE TABLE plans (
    id             TEXT        PRIMARY KEY,  -- 'free', 'premium', 'pro'
    name           TEXT        NOT NULL,
    price_monthly  DECIMAL(8,2),
    price_yearly   DECIMAL(8,2),
    features       JSONB       NOT NULL DEFAULT '{}'
);

INSERT INTO plans (id, name, price_monthly, price_yearly, features) VALUES
  ('free',    'Free',    NULL, NULL,  '{"ai_suggestions": false, "show_ads": true,  "multi_traveler": false}'),
  ('premium', 'Premium', 4.99, 49.99, '{"ai_suggestions": true,  "show_ads": false, "multi_traveler": false}'),
  ('pro',     'Pro',     9.99, 99.99, '{"ai_suggestions": true,  "show_ads": false, "multi_traveler": true}');

CREATE TABLE subscriptions (
    id                   UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id              UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id              TEXT        NOT NULL REFERENCES plans(id),
    status               TEXT        NOT NULL CHECK (status IN ('trial', 'active', 'cancelled', 'expired')),
    started_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at           TIMESTAMPTZ,
    cancelled_at         TIMESTAMPTZ,
    store                TEXT        CHECK (store IN ('apple', 'google', 'stripe')),
    store_product_id     TEXT,
    store_transaction_id TEXT,
    auto_renew           BOOLEAN     NOT NULL DEFAULT TRUE,
    trial_end            DATE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_status  ON subscriptions(status);
