package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class TopPriceAssignmentStartCmd(
    iid: InstrumentId,
    direction: OrderDirection,
    val oneStepOver: Boolean,
    refreshNotifierProperties: NotifierProperties?
) : LimitOrderAssignmentStartCmd<TopPriceAssignment>(
    iid = iid,
    direction = direction,
    refreshNotifierProperties = refreshNotifierProperties
)