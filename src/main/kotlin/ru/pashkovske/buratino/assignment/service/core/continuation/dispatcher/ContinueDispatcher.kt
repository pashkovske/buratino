package ru.pashkovske.buratino.assignment.service.core.continuation.dispatcher

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import java.util.UUID

interface ContinueDispatcher {

    fun getContinuer(assignmentId: UUID): ContinuousAssignmentContinuer<out ContinuousAssignment<out Assignment>>
}
