-- Rename status columns to state in fractional_spread table
ALTER TABLE assignment.fractional_spread
    RENAME COLUMN status TO state;

ALTER TABLE assignment.fractional_spread
    RENAME COLUMN refresh_scheduling_status TO refresh_scheduling_state;

-- Rename status columns to state in top_price table
ALTER TABLE assignment.top_price
    RENAME COLUMN status TO state;

ALTER TABLE assignment.top_price
    RENAME COLUMN refresh_scheduling_status TO refresh_scheduling_state;

-- Rename status columns to state in continuous_fractional_spread table
ALTER TABLE assignment.continuous_fractional_spread
    RENAME COLUMN status TO state;

ALTER TABLE assignment.continuous_fractional_spread
    RENAME COLUMN refresh_scheduling_status TO refresh_scheduling_state;

ALTER TABLE assignment.continuous_fractional_spread
    RENAME COLUMN continue_scheduling_status TO continue_scheduling_state;
