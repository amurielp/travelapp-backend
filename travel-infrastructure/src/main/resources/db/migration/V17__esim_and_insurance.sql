-- V17: Add eSIM and insurance event detail tables

CREATE TABLE esims (
    event_id              UUID PRIMARY KEY REFERENCES events(id) ON DELETE CASCADE,
    provider              VARCHAR(100),
    data_allowance_gb     DOUBLE PRECISION,
    coverage_region       VARCHAR(200),
    activation_code       VARCHAR(500),
    activation_deadline   DATE,
    purchase_status       VARCHAR(20),
    price_amount          DOUBLE PRECISION,
    price_currency        VARCHAR(3),
    purchase_platform     VARCHAR(100),
    purchased_at          TIMESTAMPTZ
);

CREATE TABLE insurances (
    event_id              UUID PRIMARY KEY REFERENCES events(id) ON DELETE CASCADE,
    provider              VARCHAR(100),
    policy_number         VARCHAR(50),
    coverage_type         VARCHAR(30),
    coverage_amount_eur   DOUBLE PRECISION,
    emergency_phone       VARCHAR(30),
    deductible_amount     DOUBLE PRECISION,
    purchase_status       VARCHAR(20),
    price_amount          DOUBLE PRECISION,
    price_currency        VARCHAR(3),
    purchased_at          TIMESTAMPTZ,
    beneficiaries         VARCHAR(500)
);
