-- ============================================================
-- V3 — Huecos detectados, tipos de documento y jobs de exportación
-- ============================================================

-- ── TRIP_GAPS — huecos detectados en el itinerario ───────────
-- El motor los calcula y persiste. El usuario puede resolverlos o ignorarlos.
CREATE TABLE trip_gaps (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id         UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,

    -- Tipo de hueco
    gap_type        TEXT NOT NULL,
    -- 'NO_TRANSPORT_BETWEEN_DESTINATIONS'
    -- 'ARRIVAL_WITHOUT_ACCOMMODATION'
    -- 'DAYS_WITHOUT_ACCOMMODATION'
    -- 'TRANSPORT_ONLY_DRAFT'
    -- 'PENDING_PAYMENT_DEADLINE'
    -- 'CANCELLATION_DEADLINE_NEAR'
    -- 'TIGHT_CONNECTION'
    -- 'FREE_DAYS'

    severity        TEXT NOT NULL DEFAULT 'WARNING',  -- 'ERROR' | 'WARNING' | 'INFO'

    -- Fechas afectadas
    affected_from   DATE NOT NULL,
    affected_to     DATE,

    -- Ciudades / destinos implicados (para NO_TRANSPORT_BETWEEN_DESTINATIONS)
    origin_city     TEXT,
    origin_iata     TEXT,
    destination_city TEXT,
    destination_iata TEXT,

    -- Eventos relacionados (nullable — para gaps sin evento específico)
    event_id_from   UUID REFERENCES events(id) ON DELETE SET NULL,
    event_id_to     UUID REFERENCES events(id) ON DELETE SET NULL,

    -- Estado de resolución
    status          TEXT NOT NULL DEFAULT 'OPEN',
    -- 'OPEN'     → detectado, sin resolver
    -- 'RESOLVED' → el usuario añadió un evento que lo cierra
    -- 'IGNORED'  → el usuario lo reconoció y lo ignoró deliberadamente
    -- 'SNOOZED'  → ignorado hasta una fecha

    ignored_reason  TEXT,       -- "Lo cubro con Uber", "viaje en barco propio"...
    snoozed_until   TIMESTAMPTZ,

    -- Sugerencia de resolución que mostrar al usuario
    suggestion_text TEXT,       -- "Añadir vuelo SIN→KUL (280km, ~1h vuelo)"

    -- Metadata de detección
    detected_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ,

    CONSTRAINT gap_type_check CHECK (gap_type IN (
        'NO_TRANSPORT_BETWEEN_DESTINATIONS','ARRIVAL_WITHOUT_ACCOMMODATION',
        'DAYS_WITHOUT_ACCOMMODATION','TRANSPORT_ONLY_DRAFT',
        'PENDING_PAYMENT_DEADLINE','CANCELLATION_DEADLINE_NEAR',
        'TIGHT_CONNECTION','FREE_DAYS'
    )),
    CONSTRAINT severity_check CHECK (severity IN ('ERROR','WARNING','INFO')),
    CONSTRAINT status_check   CHECK (status   IN ('OPEN','RESOLVED','IGNORED','SNOOZED'))
);

CREATE INDEX idx_trip_gaps_trip       ON trip_gaps(trip_id);
CREATE INDEX idx_trip_gaps_status     ON trip_gaps(trip_id, status) WHERE status = 'OPEN';
CREATE INDEX idx_trip_gaps_severity   ON trip_gaps(severity, status);
CREATE INDEX idx_trip_gaps_affected   ON trip_gaps(trip_id, affected_from);

-- ── DOCUMENTS — documentos de viaje subidos por el usuario ───
CREATE TABLE documents (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id          UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    uploaded_by      UUID NOT NULL REFERENCES users(id),
    file_name        TEXT NOT NULL,
    file_url         TEXT NOT NULL,
    file_size_bytes  BIGINT NOT NULL DEFAULT 0,
    file_type        TEXT,
    parse_status     TEXT NOT NULL DEFAULT 'PENDING',
    raw_extracted    TEXT,
    parsed_data      JSONB,
    parse_error      TEXT,
    uploaded_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT parse_status_check CHECK (parse_status IN ('PENDING','PROCESSING','DONE','FAILED','NOT_REQUIRED'))
);

CREATE INDEX idx_documents_trip ON documents(trip_id);

-- ── DOCUMENT_TYPES — catálogo de tipos de documento ──────────
-- Permite categorizar PDFs más allá de vuelo/hotel
CREATE TABLE document_types (
    id          TEXT PRIMARY KEY,          -- slug: 'flight_ticket', 'visa', 'insurance'...
    label_key   TEXT NOT NULL,             -- clave i18n
    icon        TEXT,                      -- nombre icono Tabler
    requires_ai_parse BOOLEAN DEFAULT FALSE, -- si debe pasarse por el AI parser
    sort_order  SMALLINT DEFAULT 0
);

INSERT INTO document_types VALUES
    ('flight_ticket',   'doc.flight_ticket',   'plane',           TRUE,  10),
    ('hotel_voucher',   'doc.hotel_voucher',   'building',        TRUE,  20),
    ('car_rental',      'doc.car_rental',      'car',             TRUE,  30),
    ('activity_ticket', 'doc.activity_ticket', 'ticket',          FALSE, 40),
    ('passport',        'doc.passport',        'id-badge',        FALSE, 50),
    ('visa',            'doc.visa',            'file-certificate',FALSE, 60),
    ('travel_insurance','doc.travel_insurance','shield-check',    FALSE, 70),
    ('covid_test',      'doc.covid_test',      'virus',           FALSE, 80),
    ('boarding_pass',   'doc.boarding_pass',   'armchair',        FALSE, 90),
    ('train_ticket',    'doc.train_ticket',    'train',           TRUE,  100),
    ('bus_ticket',      'doc.bus_ticket',      'bus',             TRUE,  110),
    ('other',           'doc.other',           'file',            FALSE, 999);

-- Añadir metadatos a documents
ALTER TABLE documents
    ADD COLUMN document_type_id TEXT REFERENCES document_types(id) DEFAULT 'other',
    ADD COLUMN display_name     TEXT,
    ADD COLUMN valid_from        DATE,
    ADD COLUMN valid_until       DATE,
    ADD COLUMN notes             TEXT;

-- ── EXPORT_JOBS — jobs asíncronos de exportación a PDF ───────
CREATE TABLE export_jobs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id         UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id),

    export_type     TEXT NOT NULL DEFAULT 'full_itinerary',
    -- 'full_itinerary'   → PDF con todo el viaje
    -- 'payment_report'   → reporte de gastos por medio de pago
    -- 'day_by_day'       → itinerario día a día sin detalles financieros

    status          TEXT NOT NULL DEFAULT 'PENDING',
    -- 'PENDING' | 'PROCESSING' | 'DONE' | 'FAILED'

    output_url      TEXT,       -- URL del PDF generado en S3/R2
    error_message   TEXT,
    options         JSONB DEFAULT '{}',  -- { "language": "es", "include_budget": true }

    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ
);

CREATE INDEX idx_export_jobs_user   ON export_jobs(user_id);
CREATE INDEX idx_export_jobs_status ON export_jobs(status) WHERE status IN ('PENDING','PROCESSING');

-- ── NOTIFICATION_LOG — histórico de notificaciones enviadas ──
CREATE TABLE notification_log (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    trip_id         UUID REFERENCES trips(id) ON DELETE SET NULL,
    event_id        UUID REFERENCES events(id) ON DELETE SET NULL,
    gap_id          UUID REFERENCES trip_gaps(id) ON DELETE SET NULL,

    notification_type TEXT NOT NULL,
    -- 'CANCELLATION_DEADLINE'  → vence cancelación gratuita
    -- 'CHECKIN_REMINDER'       → check-in online disponible
    -- 'EVENT_REMINDER'         → 24h/2h antes del evento
    -- 'GAP_DETECTED'           → hueco detectado en itinerario
    -- 'BUDGET_ALERT'           → presupuesto al 80%
    -- 'FLIGHT_STATUS'          → cambio de estado del vuelo

    channel         TEXT NOT NULL DEFAULT 'push',  -- 'push' | 'email' | 'in_app'
    title           TEXT NOT NULL,
    body            TEXT NOT NULL,
    sent_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    read_at         TIMESTAMPTZ,
    action_taken    TEXT         -- 'resolved_gap' | 'dismissed' | null
);

CREATE INDEX idx_notif_log_user    ON notification_log(user_id, sent_at DESC);
CREATE INDEX idx_notif_log_unread  ON notification_log(user_id) WHERE read_at IS NULL;
