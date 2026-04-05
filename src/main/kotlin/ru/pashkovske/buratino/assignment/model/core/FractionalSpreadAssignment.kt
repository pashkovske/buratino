package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.limit.order.OrderInfo
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

class FractionalSpreadAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    direction: OrderDirection,
    info: OrderInfo,
    val rate: Double
): LimitOrderAssignment(
    id = id,
    iid = iid,
    state = state,
    direction = direction,
    info = info
) {

    companion object {
        fun newAssignment(
            iid: InstrumentId,
            direction: OrderDirection,
            rate: Double
        ): FractionalSpreadAssignment {
            return FractionalSpreadAssignment(
                id = generateId(),
                iid = iid,
                state = initialState,
                direction = direction,
                info = OrderInfo(),
                rate = rate
            )
        }
    }
}