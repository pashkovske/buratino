-- Remove all scheduling columns from assignment tables and rename scheduling IDs to notifier IDs

-- fractional_spread table
ALTER TABLE assignment.fractional_spread
    DROP COLUMN IF EXISTS refresh_scheduling_period,
    DROP COLUMN IF EXISTS refresh_scheduling_task_id,
    DROP COLUMN IF EXISTS refresh_scheduling_state;
ALTER TABLE assignment.fractional_spread
    RENAME COLUMN refresh_scheduling_id TO refresh_notifier_id;

-- top_price table
ALTER TABLE assignment.top_price
    DROP COLUMN IF EXISTS refresh_scheduling_period,
    DROP COLUMN IF EXISTS refresh_scheduling_task_id,
    DROP COLUMN IF EXISTS refresh_scheduling_state;
ALTER TABLE assignment.top_price
    RENAME COLUMN refresh_scheduling_id TO refresh_notifier_id;

-- continuous_fractional_spread table
ALTER TABLE assignment.continuous_fractional_spread
    DROP COLUMN IF EXISTS refresh_scheduling_period,
    DROP COLUMN IF EXISTS refresh_scheduling_task_id,
    DROP COLUMN IF EXISTS refresh_scheduling_state,
    DROP COLUMN IF EXISTS continue_scheduling_period,
    DROP COLUMN IF EXISTS continue_scheduling_task_id,
    DROP COLUMN IF EXISTS continue_scheduling_state;
ALTER TABLE assignment.continuous_fractional_spread
    RENAME COLUMN refresh_scheduling_id TO refresh_notifier_id;
ALTER TABLE assignment.continuous_fractional_spread
    RENAME COLUMN continue_scheduling_id TO continue_notifier_id;
