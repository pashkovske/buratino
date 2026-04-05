CREATE INDEX IF NOT EXISTS idx_periodic_refresh_state ON notify.periodic_refresh(state);
CREATE INDEX IF NOT EXISTS idx_periodic_continue_state ON notify.periodic_continue(state);
