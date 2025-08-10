CREATE TABLE current_spreads (
    id BIGSERIAL PRIMARY KEY,
    instrument_uid VARCHAR(255) NOT NULL,
    min_price_units BIGINT NOT NULL,
    min_price_nanos INTEGER NOT NULL,
    max_price_units BIGINT NOT NULL,
    max_price_nanos INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_instrument_uid UNIQUE (instrument_uid)
);

CREATE INDEX idx_current_spreads_instrument_uid ON current_spreads(instrument_uid);
CREATE INDEX idx_current_spreads_min_price_units ON current_spreads(min_price_units);
CREATE INDEX idx_current_spreads_max_price_units ON current_spreads(max_price_units);
