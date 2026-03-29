package ru.pashkovske.buratino.assignment.service.core.continuation

import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentContinuer<ContinuousA: ContinuousAssignment<*>> {

    fun continueAssignment(id: UUID): ContinuousA
}
