package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

abstract class AssignmentPostgreRow<A : Assignment>(
    open val id: UUID,
    open val instrumentId: String,
    open val state: AssignmentState,
    open val refreshNotifierId: UUID?
)
