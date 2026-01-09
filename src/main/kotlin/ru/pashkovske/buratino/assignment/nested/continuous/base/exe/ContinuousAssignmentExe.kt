package ru.pashkovske.buratino.assignment.nested.continuous.base.exe

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.exe.AssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentExe<CA : ContinuousAssignment<out Assignment>>: AssignmentExe<CA> {

    fun continueAssignment(id: UUID): CA
}
