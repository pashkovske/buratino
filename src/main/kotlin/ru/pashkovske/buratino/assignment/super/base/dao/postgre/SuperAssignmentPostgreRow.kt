package ru.pashkovske.buratino.assignment.`super`.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import java.time.Duration
import java.util.UUID

abstract class SuperAssignmentPostgreRow<NestedA : Assignment>(
    id: UUID,
    instrumentId: String,
    status: AssignmentState,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingState: SchedulingState?,
    open val nestedAssignmentId: UUID
) : AssignmentPostgreRow<NestedA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState
)
