package ru.pashkovske.buratino.assignment.parent.continuous.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.parent.base.dao.postgre.ParentAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
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
    childAssignmentId: UUID,
    open val continueSchedulingPeriod: Duration?,
    open val continueSchedulingTaskId: UUID?,
    open val continueSchedulingState: SchedulingState?
) : ParentAssignmentPostgreRow<ContinuousA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    childAssignmentId = childAssignmentId
)