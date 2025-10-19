package ru.pashkovske.buratino.assignment.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.InstrumentAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignment<T: InstrumentAssignment>(
    override val iid: InstrumentId,
    open var currentAssignment: T
): InstrumentAssignment(
    iid = iid
)