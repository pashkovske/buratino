package ru.pashkovske.buratino.assignment.nested.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import java.time.Duration
import java.util.UUID

abstract class SuperAssignmentPostgreRow<NestedA : Assignment>(
    id: UUID,
    instrumentId: String,
    status: AssignmentStatus,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingStatus: SchedulingStatus?,
    open val nestedAssignmentId: UUID
) : AssignmentPostgreRow<NestedA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingStatus = refreshSchedulingStatus
)
