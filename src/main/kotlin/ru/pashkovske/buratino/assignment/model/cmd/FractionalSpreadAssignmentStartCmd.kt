package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class FractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val rate: Double,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : LimitOrderAssignmentStartCmd<FractionalSpreadAssignment>(
    iid = iid,
    direction = direction,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)