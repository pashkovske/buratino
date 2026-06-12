package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment
import java.util.UUID

abstract class RepeatableAssignmentPostgreRow<
    ChildA : Assignment,
    RepeatableA : RepeatableAssignment<ChildA>
    >(
    id: UUID,
    instrumentId: String,
    state: AssignmentState,
    refreshNotifierId: UUID?,
    childAssignmentId: UUID
) : ParentAssignmentPostgreRow<RepeatableA>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId,
    childAssignmentId = childAssignmentId
)
