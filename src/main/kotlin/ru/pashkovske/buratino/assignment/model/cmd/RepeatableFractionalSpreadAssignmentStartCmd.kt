package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class RepeatableFractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    val direction: OrderDirection,
    val rate: Double,
    refreshNotifierProperties: NotifierProperties?
) : RepeatableAssignmentStartCmd<RepeatableFractionalSpreadAssignment, FractionalSpreadAssignment>(
    iid = iid,
    refreshNotifierProperties = refreshNotifierProperties
)
