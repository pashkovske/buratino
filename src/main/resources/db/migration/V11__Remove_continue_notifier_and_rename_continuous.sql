-- Remove continue_notifier_id column from continuous_fractional_spread table
-- Rename continuous_fractional_spread table to repeatable_fractional_spread
-- Drop the periodic_continue notifier table (continue operation is removed)

ALTER TABLE assignment.continuous_fractional_spread
    DROP COLUMN IF EXISTS continue_notifier_id;

ALTER TABLE assignment.continuous_fractional_spread
    RENAME TO repeatable_fractional_spread;

DROP TABLE IF EXISTS notify.periodic_continue;
