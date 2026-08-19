-- V22: Desbloqueos temporales por anuncio recompensado + feature flag de publicidad
-- Un rewarded ad desbloquea una feature premium durante un TTL (ej: 24h de sugerencias IA).

CREATE TABLE rewarded_unlocks (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    feature_key TEXT        NOT NULL,
    unlocked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_rewarded_unlocks_user_feature ON rewarded_unlocks(user_id, feature_key);
CREATE INDEX idx_rewarded_unlocks_expires      ON rewarded_unlocks(expires_at);

INSERT INTO feature_flags (key, enabled, enabled_for, description)
VALUES ('show_ads', TRUE, 'free', 'Mostrar publicidad a usuarios del plan gratuito');
