package ru.pashkovske.buratino.assignment.continuous.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.continuous.base.model.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentExe<T : ContinuousAssignment<out Assignment>>: AssignmentExe<T> {
    fun continueAssignment(id: UUID): T
}
