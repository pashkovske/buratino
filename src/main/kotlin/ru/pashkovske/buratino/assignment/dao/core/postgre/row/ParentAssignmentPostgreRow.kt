package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

abstract class ParentAssignmentPostgreRow<ChildA : Assignment>(
    id: UUID,
    instrumentId: String,
    state: AssignmentState,
    refreshNotifierId: UUID?,
    open val childAssignmentId: UUID
) : AssignmentPostgreRow<ChildA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId
)
