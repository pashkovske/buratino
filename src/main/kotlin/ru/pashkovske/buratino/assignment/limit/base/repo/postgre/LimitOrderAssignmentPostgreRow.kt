package ru.pashkovske.buratino.assignment.limit.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.OrderDirection
import java.time.Duration
import java.time.Instant
import java.util.UUID

abstract class LimitOrderAssignmentPostgreRow<LA : LimitOrderAssignment>(
    id: UUID,
    instrumentId: String,
    status: AssignmentStatus,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingStatus: SchedulingStatus?,
    open val orderDirection: OrderDirection,
    open val orderId: String?,
    open val lastOrderUpdate: Instant
) : AssignmentPostgreRow<LA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingStatus = refreshSchedulingStatus,
)
