package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class ContinuousFractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    val direction: OrderDirection,
    val rate: Double,
    continueNotifierProperties: NotifierProperties?,
    refreshNotifierProperties: NotifierProperties?
) : ContinuousAssignmentStartCmd<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>(
    iid = iid,
    continueNotifierProperties = continueNotifierProperties,
    refreshNotifierProperties = refreshNotifierProperties
)