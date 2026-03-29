package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.time.Duration
import java.util.UUID

abstract class ContinuousAssignmentPostgreRow<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    id: UUID,
    instrumentId: String,
    state: AssignmentState,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingState: SchedulingState?,
    refreshSchedulingId: UUID?,
    childAssignmentId: UUID,
    open val continueSchedulingPeriod: Duration?,
    open val continueSchedulingTaskId: UUID?,
    open val continueSchedulingState: SchedulingState?,
    open val continueSchedulingId: UUID?
) : ParentAssignmentPostgreRow<ContinuousA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    refreshSchedulingId = refreshSchedulingId,
    childAssignmentId = childAssignmentId
)