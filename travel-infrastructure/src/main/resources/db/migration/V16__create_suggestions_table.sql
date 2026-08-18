-- V16: tabla de sugerencias IA persistidas por viaje y fecha
-- Permite que accept/dismiss funcionen con un UUID estable entre peticiones.

CREATE TABLE suggestions (
    id                   UUID         PRIMARY KEY,
    trip_id              UUID         NOT NULL,
    date                 DATE         NOT NULL,
    name                 VARCHAR(200) NOT NULL,
    category             VARCHAR(100),
    description          TEXT,
    reasoning            TEXT,
    estimated_duration_min INTEGER,
    estimated_cost_eur   DOUBLE PRECISION,
    best_time_of_day     VARCHAR(20),
    latitude             DOUBLE PRECISION,
    longitude            DOUBLE PRECISION,
    website_url          VARCHAR(500),
    source               VARCHAR(50),
    status               VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_suggestions_trip_date ON suggestions (trip_id, date);
