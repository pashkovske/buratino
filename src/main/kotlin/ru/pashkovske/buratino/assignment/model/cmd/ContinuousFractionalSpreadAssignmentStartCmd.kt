package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
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