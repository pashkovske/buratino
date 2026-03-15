package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

class ContinuousFractionalSpreadAssignmentStartCmd(
    iid: InstrumentId,
    val direction: OrderDirection,
    val rate: Double,
    continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : ContinuousAssignmentStartCmd<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>(
    iid = iid,
    continueAssignmentSchedulingProperties = continueAssignmentSchedulingProperties,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)
