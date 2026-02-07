package ru.pashkovske.buratino.assignment.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import java.time.Duration
import java.util.UUID

abstract class AssignmentPostgreRow<A : Assignment>(
    open val id: UUID,
    open val instrumentId: String,
    open val status: AssignmentStatus,
    open val refreshSchedulingPeriod: Duration?,
    open val refreshSchedulingTaskId: UUID?,
    open val refreshSchedulingStatus: SchedulingStatus?
)