package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class FractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val rate: Double,
    refreshNotifierProperties: NotifierProperties?
) : LimitOrderAssignmentStartCmd<FractionalSpreadAssignment>(
    iid = iid,
    direction = direction,
    refreshNotifierProperties = refreshNotifierProperties
)