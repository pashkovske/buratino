package ru.pashkovske.buratino.assignment.`super`.continuous.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
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
    status: AssignmentStatus,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingStatus: SchedulingStatus?,
    nestedAssignmentId: UUID,
    open val continueSchedulingPeriod: Duration?,
    open val continueSchedulingTaskId: UUID?,
    open val continueSchedulingStatus: SchedulingStatus?
) : SuperAssignmentPostgreRow<ContinuousA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingStatus = refreshSchedulingStatus,
    nestedAssignmentId = nestedAssignmentId
)