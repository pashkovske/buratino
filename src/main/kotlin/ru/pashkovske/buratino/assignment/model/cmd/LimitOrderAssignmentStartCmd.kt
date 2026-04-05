package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

abstract class LimitOrderAssignmentStartCmd<LimitA : LimitOrderAssignment>(
    iid: InstrumentId,
    val direction: OrderDirection,
    refreshNotifierProperties: NotifierProperties?
) : AssignmentStartCmd<LimitA>(
    iid = iid,
    refreshNotifierProperties = refreshNotifierProperties
)