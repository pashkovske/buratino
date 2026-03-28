package ru.pashkovske.buratino.assignment.parent.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import java.time.Duration
import java.util.UUID

abstract class ParentAssignmentPostgreRow<ChildA : Assignment>(
    id: UUID,
    instrumentId: String,
    state: AssignmentState,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingState: SchedulingState?,
    refreshSchedulingId: UUID?,
    open val childAssignmentId: UUID
) : AssignmentPostgreRow<ChildA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    refreshSchedulingId = refreshSchedulingId
)
