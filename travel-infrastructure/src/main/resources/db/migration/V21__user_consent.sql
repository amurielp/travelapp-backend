-- V21: Consentimiento GDPR por usuario
-- Recoge el resultado del diálogo Google UMP antes de mostrar publicidad personalizada.

CREATE TABLE user_consent (
    user_id          UUID        PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    ads_personalized BOOLEAN     NOT NULL DEFAULT FALSE,
    analytics        BOOLEAN     NOT NULL DEFAULT FALSE,
    consent_version  TEXT        NOT NULL,
    consented_at     TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
