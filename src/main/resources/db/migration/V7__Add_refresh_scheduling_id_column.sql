ALTER TABLE assignment.fractional_spread
    ADD COLUMN IF NOT EXISTS refresh_scheduling_id UUID;

ALTER TABLE assignment.top_price
    ADD COLUMN IF NOT EXISTS refresh_scheduling_id UUID;

ALTER TABLE assignment.continuous_fractional_spread
    ADD COLUMN IF NOT EXISTS refresh_scheduling_id UUID,
    ADD COLUMN IF NOT EXISTS continue_scheduling_id UUID;
