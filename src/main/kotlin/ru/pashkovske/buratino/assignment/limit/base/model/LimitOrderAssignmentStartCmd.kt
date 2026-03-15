package ru.pashkovske.buratino.assignment.limit.base.model

import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

abstract class LimitOrderAssignmentStartCmd<LimitA : LimitOrderAssignment>(
    iid: InstrumentId,
    val direction: OrderDirection,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : AssignmentStartCmd<LimitA>(
    iid = iid,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)
