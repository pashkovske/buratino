package ru.pashkovske.buratino.assignment.limit.top.price.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class TopPriceAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val oneStepOver: Boolean,
    refreshSchedulingProperties: SchedulingProperties?
) : LimitOrderAssignmentStartCmd<TopPriceAssignment>(
    iid = iid,
    direction = direction,
    refreshSchedulingProperties = refreshSchedulingProperties
)
