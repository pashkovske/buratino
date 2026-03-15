package ru.pashkovske.buratino.assignment.limit.top.price.model

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class TopPriceAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val oneStepOver: Boolean,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : LimitOrderAssignmentStartCmd<TopPriceAssignment>(
    iid = iid,
    direction = direction,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)
