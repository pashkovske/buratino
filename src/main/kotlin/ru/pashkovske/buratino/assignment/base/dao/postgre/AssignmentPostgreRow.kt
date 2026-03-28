package ru.pashkovske.buratino.assignment.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import java.time.Duration
import java.util.UUID

abstract class AssignmentPostgreRow<A : Assignment>(
    open val id: UUID,
    open val instrumentId: String,
    open val state: AssignmentState,
    open val refreshSchedulingPeriod: Duration?,
    open val refreshSchedulingTaskId: UUID?,
    open val refreshSchedulingState: SchedulingState?,
    open val refreshSchedulingId: UUID?
)