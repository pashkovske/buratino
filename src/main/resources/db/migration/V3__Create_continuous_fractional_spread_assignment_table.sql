CREATE TABLE IF NOT EXISTS assignment.continuous_fractional_spread
(
    id                          UUID PRIMARY KEY,
    instrument_id               VARCHAR(255) NOT NULL,
    status                      VARCHAR(50)  NOT NULL,
    refresh_scheduling_period   VARCHAR(255),
    refresh_scheduling_task_id  UUID,
    refresh_scheduling_status   VARCHAR(50),
    nested_assignment_id        UUID         NOT NULL,
    continue_scheduling_period  VARCHAR(255),
    continue_scheduling_task_id UUID,
    continue_scheduling_status  VARCHAR(50)
);
