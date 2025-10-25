package ru.pashkovske.buratino.assignment.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignment<T: Assignment>(
    override val iid: InstrumentId,
    open var currentAssignment: T
): Assignment(
    iid = iid
)