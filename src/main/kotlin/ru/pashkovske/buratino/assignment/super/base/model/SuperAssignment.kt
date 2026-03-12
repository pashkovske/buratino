package ru.pashkovske.buratino.assignment.`super`.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class SuperAssignment<Nested: Assignment>(
    id: UUID,
    iid: InstrumentId,
    status: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    var nested: Nested
): Assignment(
    id = id,
    iid = iid,
    status = status,
    refreshSchedulingProperties = refreshSchedulingProperties
)
