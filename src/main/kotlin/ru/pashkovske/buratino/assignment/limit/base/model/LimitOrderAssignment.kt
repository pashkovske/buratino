package ru.pashkovske.buratino.assignment.limit.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

abstract class LimitOrderAssignment(
    id: UUID,
    iid: InstrumentId,
    status: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    val direction: OrderDirection,
    val info: OrderInfo
): Assignment(
    id = id,
    iid = iid,
    status = status,
    refreshSchedulingProperties = refreshSchedulingProperties
) {

    companion object {
        fun initialOrderInfo(): OrderInfo {
            return OrderInfo()
        }
    }
}
