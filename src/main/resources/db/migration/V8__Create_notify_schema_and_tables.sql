CREATE SCHEMA IF NOT EXISTS notify;

CREATE TABLE IF NOT EXISTS notify.periodic_refresh (
    id UUID PRIMARY KEY,
    assignment_id UUID NOT NULL,
    task_id UUID,
    state VARCHAR(50) NOT NULL,
    period_nanos BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_periodic_refresh_assignment_id ON notify.periodic_refresh(assignment_id);

CREATE TABLE IF NOT EXISTS notify.periodic_continue (
    id UUID PRIMARY KEY,
    assignment_id UUID NOT NULL,
    task_id UUID,
    state VARCHAR(50) NOT NULL,
    period_nanos BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_periodic_continue_assignment_id ON notify.periodic_continue(assignment_id);
