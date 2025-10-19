package ru.pashkovske.buratino.assignment.continuous.base.service

import ru.pashkovske.buratino.assignment.base.model.InstrumentAssignment
import ru.pashkovske.buratino.assignment.base.service.AssignmentExecutor
import ru.pashkovske.buratino.assignment.continuous.base.model.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentExecutor<T : ContinuousAssignment<out InstrumentAssignment>>: AssignmentExecutor<T> {
    fun continueAssignment(id: UUID): T
}
