package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class AssignmentStartCmd<A : Assignment>(
    val iid: InstrumentId,
    val refreshSchedulingProperties: SchedulingProperties?
)
