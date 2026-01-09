package ru.pashkovske.buratino.assignment.nested.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class SuperAssignment<Nested: Assignment>(
    iid: InstrumentId,
    refreshSchedulingProperties: SchedulingProperties?,
    var nested: Nested
): Assignment(
    iid = iid,
    refreshSchedulingProperties = refreshSchedulingProperties
)
