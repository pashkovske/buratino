package ru.pashkovske.buratino.assignment.limit.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class FractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val rate: Double,
    refreshSchedulingProperties: SchedulingProperties?
) : LimitOrderAssignmentStartCmd<FractionalSpreadAssignment>(
    iid = iid,
    direction = direction,
    refreshSchedulingProperties = refreshSchedulingProperties
)
