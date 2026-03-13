package ru.pashkovske.buratino.assignment.limit.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

class FractionalSpreadAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    direction: OrderDirection,
    info: OrderInfo,
    val rate: Double
): LimitOrderAssignment(
    id = id,
    iid = iid,
    state = state,
    refreshSchedulingProperties = refreshSchedulingProperties,
    direction = direction,
    info = info
) {

    companion object {
        fun newAssignment(
            iid: InstrumentId,
            direction: OrderDirection,
            refreshSchedulingProperties: SchedulingProperties?,
            rate: Double
        ): FractionalSpreadAssignment {
            return FractionalSpreadAssignment(
                id = generateId(),
                iid = iid,
                state = initialState,
                refreshSchedulingProperties = refreshSchedulingProperties,
                direction = direction,
                info = OrderInfo(),
                rate = rate
            )
        }
    }
}
