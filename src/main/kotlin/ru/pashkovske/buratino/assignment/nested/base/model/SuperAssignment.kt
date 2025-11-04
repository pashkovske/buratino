package ru.pashkovske.buratino.assignment.nested.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class SuperAssignment<Nested: Assignment>(
    iid: InstrumentId,
    var nested: Nested
): Assignment(
    iid = iid
)
