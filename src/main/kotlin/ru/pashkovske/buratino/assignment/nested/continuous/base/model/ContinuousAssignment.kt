package ru.pashkovske.buratino.assignment.nested.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignment<T: Assignment>(
    override val iid: InstrumentId,
    nested: T
): SuperAssignment<T>(
    iid = iid,
    nested = nested
)