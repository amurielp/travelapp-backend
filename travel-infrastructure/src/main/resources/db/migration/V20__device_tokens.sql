-- V20: Tokens de dispositivo para push notifications via Firebase Cloud Messaging (FCM)
-- Un usuario puede tener múltiples dispositivos. El token FCM es único por dispositivo.

CREATE TABLE device_tokens (
    id            UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    platform      TEXT        NOT NULL CHECK (platform IN ('ios', 'android', 'web')),
    fcm_token     TEXT        NOT NULL UNIQUE,
    device_model  TEXT,
    app_version   TEXT,
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE,
    registered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_device_tokens_user_id ON device_tokens(user_id);
CREATE INDEX idx_device_tokens_active  ON device_tokens(user_id) WHERE is_active = TRUE;
