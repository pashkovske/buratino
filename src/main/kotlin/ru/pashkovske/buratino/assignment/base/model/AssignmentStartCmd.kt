package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class AssignmentStartCmd<A : Assignment>(
    val iid: InstrumentId,
    val refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
)
