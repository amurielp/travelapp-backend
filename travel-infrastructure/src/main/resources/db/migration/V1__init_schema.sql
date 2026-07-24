-- TravelApp — migración inicial
-- Flyway ejecuta esto una sola vez al arrancar la app

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id      TEXT NOT NULL UNIQUE,  -- sub del JWT de Keycloak
    email            TEXT NOT NULL UNIQUE,
    name             TEXT NOT NULL,
    avatar_url       TEXT,
    preferences      JSONB NOT NULL DEFAULT '{}',
    plan             TEXT NOT NULL DEFAULT 'free',
    plan_expires_at  TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE trips (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title            TEXT NOT NULL,
    description      TEXT,
    cover_image_url  TEXT,
    status           TEXT NOT NULL DEFAULT 'PLANNING',
    start_date       DATE NOT NULL,
    end_date         DATE NOT NULL,
    base_currency    TEXT NOT NULL DEFAULT 'EUR',
    is_public        BOOLEAN NOT NULL DEFAULT FALSE,
    public_slug      TEXT UNIQUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_dates CHECK (end_date >= start_date)
);

CREATE TABLE events (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id          UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    document_id      UUID,
    type             TEXT NOT NULL,
    title            TEXT NOT NULL,
    notes            TEXT,
    color            TEXT,
    start_datetime   TIMESTAMPTZ NOT NULL,
    end_datetime     TIMESTAMPTZ,
    all_day          BOOLEAN NOT NULL DEFAULT FALSE,
    timezone         TEXT NOT NULL DEFAULT 'Europe/Madrid',
    status           TEXT NOT NULL DEFAULT 'CONFIRMED',
    source           TEXT NOT NULL DEFAULT 'MANUAL',
    location_name    TEXT,
    latitude         DECIMAL(10,7),
    longitude        DECIMAL(10,7),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE flights (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id             UUID NOT NULL UNIQUE REFERENCES events(id) ON DELETE CASCADE,
    airline              TEXT,
    flight_number        TEXT,
    origin_city          TEXT, origin_airport TEXT, origin_iata TEXT, origin_terminal TEXT,
    destination_city     TEXT, destination_airport TEXT, destination_iata TEXT, destination_terminal TEXT,
    departure_at         TIMESTAMPTZ NOT NULL,
    arrival_at           TIMESTAMPTZ NOT NULL,
    seat                 TEXT,
    cabin_class          TEXT DEFAULT 'economy',
    booking_ref          TEXT,
    baggage_allowance    TEXT
);

CREATE TABLE accommodations (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id             UUID NOT NULL UNIQUE REFERENCES events(id) ON DELETE CASCADE,
    name                 TEXT NOT NULL,
    accommodation_type   TEXT DEFAULT 'hotel',
    address              TEXT, city TEXT, country TEXT,
    latitude             DECIMAL(10,7), longitude DECIMAL(10,7),
    check_in_date        DATE NOT NULL,
    check_out_date       DATE NOT NULL,
    check_in_time        TIME DEFAULT '15:00',
    check_out_time       TIME DEFAULT '12:00',
    total_nights         INTEGER GENERATED ALWAYS AS (check_out_date - check_in_date) STORED,
    booking_ref          TEXT, booking_platform TEXT,
    includes_breakfast   BOOLEAN DEFAULT FALSE
);

CREATE TABLE budgets (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id     UUID NOT NULL UNIQUE REFERENCES trips(id) ON DELETE CASCADE,
    currency    TEXT NOT NULL DEFAULT 'EUR',
    total_limit DECIMAL(12,2),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE budget_items (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    budget_id        UUID NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    event_id         UUID REFERENCES events(id) ON DELETE SET NULL,
    category         TEXT NOT NULL,
    description      TEXT NOT NULL,
    amount_estimated DECIMAL(12,2) DEFAULT 0,
    amount_actual    DECIMAL(12,2),
    currency         TEXT NOT NULL DEFAULT 'EUR',
    is_paid          BOOLEAN NOT NULL DEFAULT FALSE,
    paid_at          TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE wishlist_items (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id               UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    name                  TEXT NOT NULL,
    category              TEXT,
    destination_city      TEXT,
    latitude              DECIMAL(10,7), longitude DECIMAL(10,7),
    external_place_id     TEXT,
    source                TEXT,
    priority              SMALLINT DEFAULT 2,
    converted_to_event_id UUID REFERENCES events(id) ON DELETE SET NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cached_places (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    google_place_id  TEXT UNIQUE NOT NULL,
    city             TEXT NOT NULL,
    country          TEXT NOT NULL,
    name             TEXT NOT NULL,
    categories       TEXT[],
    rating           DECIMAL(2,1),
    review_count     INTEGER,
    latitude         DECIMAL(10,7), longitude DECIMAL(10,7),
    photo_url        TEXT,
    price_level      SMALLINT,
    cached_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at       TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '30 days'
);

CREATE TABLE feature_flags (
    key          TEXT PRIMARY KEY,
    enabled      BOOLEAN NOT NULL DEFAULT FALSE,
    enabled_for  TEXT DEFAULT 'all',
    description  TEXT,
    updated_at   TIMESTAMPTZ DEFAULT NOW()
);

INSERT INTO feature_flags VALUES
  ('ai_pdf_parsing',  FALSE, 'premium', 'Parsing PDFs con Claude'),
  ('ai_suggestions',  FALSE, 'premium', 'Sugerencias IA personalizadas'),
  ('ai_trip_summary', FALSE, 'premium', 'Resumen IA del viaje');

-- Índices
CREATE INDEX idx_trips_user_id     ON trips(user_id);
CREATE INDEX idx_events_trip_id    ON events(trip_id);
CREATE INDEX idx_events_start      ON events(start_datetime);
CREATE INDEX idx_budget_items_b    ON budget_items(budget_id);
CREATE INDEX idx_cached_places_city ON cached_places(city);
CREATE INDEX idx_cached_places_exp  ON cached_places(expires_at);
CREATE INDEX idx_cached_places_cat  ON cached_places USING gin(categories);
