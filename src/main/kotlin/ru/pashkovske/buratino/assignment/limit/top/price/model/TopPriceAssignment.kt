package ru.pashkovske.buratino.assignment.limit.top.price.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class TopPriceAssignment(
    iid: InstrumentId,
    refreshSchedulingProperties: SchedulingProperties?,
    direction: OrderDirection,
    val oneStepOver: Boolean = false
) :  LimitedOrderAssignment(
    iid = iid,
    refreshSchedulingProperties = refreshSchedulingProperties,
    direction = direction
)