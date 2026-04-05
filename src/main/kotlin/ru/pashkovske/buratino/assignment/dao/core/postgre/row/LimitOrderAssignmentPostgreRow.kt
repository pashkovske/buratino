package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

abstract class LimitOrderAssignmentPostgreRow<LA : LimitOrderAssignment>(
    id: UUID,
    instrumentId: String,
    state: AssignmentState,
    refreshNotifierId: UUID?,
    open val orderDirection: OrderDirection,
    open val orderId: String?
) : AssignmentPostgreRow<LA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId
)
