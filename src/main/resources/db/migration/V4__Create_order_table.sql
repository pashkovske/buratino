CREATE TABLE IF NOT EXISTS order_table (
    id VARCHAR(255) PRIMARY KEY,
    
    -- InstrumentId
    instrument_id VARCHAR(255) NOT NULL,
    
    -- OrderRequest fields
    request_type VARCHAR(50) NOT NULL,
    request_direction VARCHAR(10) NOT NULL,
    request_lots BIGINT NOT NULL,
    request_idempotency_token UUID,
    request_price_unit BIGINT,
    request_price_nano INTEGER,
    request_price_currency VARCHAR(10),
    
    -- OrderCommitResult fields
    commit_commission_unit BIGINT NOT NULL,
    commit_commission_nano INTEGER NOT NULL,
    commit_commission_currency VARCHAR(10) NOT NULL,
    commit_time TIMESTAMP NOT NULL,
    
    -- OrderInstantInfo fields
    current_remaining_lots BIGINT NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    last_update TIMESTAMP NOT NULL
);
