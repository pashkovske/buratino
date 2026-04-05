package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.limit.order.OrderInfo
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

sealed class LimitOrderAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    val direction: OrderDirection,
    val info: OrderInfo
): Assignment(
    id = id,
    iid = iid,
    state = state
) {

    companion object {
        fun initialOrderInfo(): OrderInfo {
            return OrderInfo()
        }
    }
}