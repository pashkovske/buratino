package ru.pashkovske.buratino.assignment.limit.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.OrderDirection
import java.time.Duration
import java.util.UUID

abstract class LimitOrderAssignmentPostgreRow<LA : LimitOrderAssignment>(
    id: UUID,
    instrumentId: String,
    status: AssignmentState,
    refreshSchedulingPeriod: Duration?,
    refreshSchedulingTaskId: UUID?,
    refreshSchedulingState: SchedulingState?,
    open val orderDirection: OrderDirection,
    open val orderId: String?
) : AssignmentPostgreRow<LA>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
)
