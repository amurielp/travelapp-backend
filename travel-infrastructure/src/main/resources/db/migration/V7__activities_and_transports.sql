-- V7 — Tablas de detalle para actividades y transportes

CREATE TABLE activities (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id          UUID NOT NULL UNIQUE REFERENCES events(id) ON DELETE CASCADE,
    venue_name        TEXT,
    address           TEXT,
    city              TEXT,
    latitude          DOUBLE PRECISION,
    longitude         DOUBLE PRECISION,
    category          TEXT,
    booking_ref       TEXT,
    ticket_url        TEXT,
    num_people        INTEGER,
    external_place_id TEXT,
    rating            DECIMAL(3,1),
    website_url       TEXT,
    phone             TEXT,
    purchase_status   purchase_status NOT NULL DEFAULT 'DRAFT',
    payment_method_id UUID REFERENCES payment_methods(id) ON DELETE SET NULL,
    price_amount      DECIMAL(12,2),
    price_currency    TEXT DEFAULT 'EUR',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE transports (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id              UUID NOT NULL UNIQUE REFERENCES events(id) ON DELETE CASCADE,
    transport_type        TEXT,
    provider              TEXT,
    origin_name           TEXT,
    origin_address        TEXT,
    origin_latitude       DOUBLE PRECISION,
    origin_longitude      DOUBLE PRECISION,
    destination_name      TEXT,
    destination_address   TEXT,
    destination_latitude  DOUBLE PRECISION,
    destination_longitude DOUBLE PRECISION,
    booking_ref           TEXT,
    seat_number           TEXT,
    vehicle_details       TEXT,
    license_plate         TEXT,
    pickup_instructions   TEXT,
    purchase_status       purchase_status NOT NULL DEFAULT 'DRAFT',
    payment_method_id     UUID REFERENCES payment_methods(id) ON DELETE SET NULL,
    price_amount          DECIMAL(12,2),
    price_currency        TEXT DEFAULT 'EUR',
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activities_event     ON activities(event_id);
CREATE INDEX idx_transports_event     ON transports(event_id);
