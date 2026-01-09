package ru.pashkovske.buratino.assignment.limit.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class FractionalSpreadAssignment(
    iid: InstrumentId,
    refreshSchedulingProperties: SchedulingProperties?,
    direction: OrderDirection,
    val rate: Double
): LimitedOrderAssignment(
    iid = iid,
    direction = direction,
    refreshSchedulingProperties = refreshSchedulingProperties
)
