package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

sealed class RepeatableAssignment<ChildA : Assignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    child: ChildA
) : ParentAssignment<ChildA>(
    id = id,
    iid = iid,
    state = state,
    child = child
)
