package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

sealed class ParentAssignment<ChildA: Assignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    var child: ChildA
): Assignment(
    id = id,
    iid = iid,
    state = state
)