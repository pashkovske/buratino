package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class ContinuousFractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    val direction: OrderDirection,
    val rate: Double,
    continueSchedulingProperties: SchedulingProperties?,
    refreshSchedulingProperties: SchedulingProperties?
) : ContinuousAssignmentStartCmd<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>(
    iid = iid,
    continueSchedulingProperties = continueSchedulingProperties,
    refreshSchedulingProperties = refreshSchedulingProperties
)
