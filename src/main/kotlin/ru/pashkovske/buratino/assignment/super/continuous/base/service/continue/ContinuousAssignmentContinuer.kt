package ru.pashkovske.buratino.assignment.`super`.continuous.base.service.`continue`

import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentContinuer<ContinuousA: ContinuousAssignment<*>> {

    fun continueAssignment(id: UUID): ContinuousA
}
