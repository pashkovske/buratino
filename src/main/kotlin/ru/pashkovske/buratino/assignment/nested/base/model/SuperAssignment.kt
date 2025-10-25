package ru.pashkovske.buratino.assignment.nested.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class SuperAssignment<Nested: Assignment>(
    override val iid: InstrumentId,
    open var nested: Nested
): Assignment(
    iid = iid
)
