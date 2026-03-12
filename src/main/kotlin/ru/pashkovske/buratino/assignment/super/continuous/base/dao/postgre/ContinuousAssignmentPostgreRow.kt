package ru.pashkovske.buratino.assignment.`super`.continuous.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.`super`.base.dao.postgre.SuperAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import java.time.Duration
import java.util.UUID

abstract class ContinuousAssignmentPostgreRow<
    NestedA : Assignment,
    ContinuousA : ContinuousAssignment<NestedA>
    >(
    id: UUID,
    instrumentId: String,
    status: AssignmentState,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingState: SchedulingState?,
    nestedAssignmentId: UUID,
    open val continueSchedulingPeriod: Duration?,
    open val continueSchedulingTaskId: UUID?,
    open val continueSchedulingState: SchedulingState?
) : SuperAssignmentPostgreRow<ContinuousA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    nestedAssignmentId = nestedAssignmentId
)