CREATE TABLE IF NOT EXISTS fractional_spread_assignment_row
(
    id                         UUID PRIMARY KEY,
    instrument_id              VARCHAR(255)     NOT NULL,
    status                     VARCHAR(50)      NOT NULL,
    order_direction            VARCHAR(10)      NOT NULL,
    rate                       DOUBLE PRECISION NOT NULL,
    refresh_scheduling_period  VARCHAR(255),
    refresh_scheduling_task_id UUID,
    refresh_scheduling_status  VARCHAR(50),
    order_id                   VARCHAR(255),
    last_order_update          TIMESTAMP        NOT NULL
);