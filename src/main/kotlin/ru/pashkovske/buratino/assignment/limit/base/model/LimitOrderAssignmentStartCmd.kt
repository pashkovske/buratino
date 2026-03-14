package ru.pashkovske.buratino.assignment.limit.base.model

import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

abstract class LimitOrderAssignmentStartCmd<LimitA : LimitOrderAssignment>(
    iid: InstrumentId,
    val direction: OrderDirection,
    refreshSchedulingProperties: SchedulingProperties?
) : AssignmentStartCmd<LimitA>(
    iid = iid,
    refreshSchedulingProperties = refreshSchedulingProperties
)
