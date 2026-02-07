package ru.pashkovske.buratino.assignment.`super`.continuous.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import java.util.UUID

interface ContinuousAssignmentExe<CA : ContinuousAssignment<out Assignment>>: AssignmentExe<CA> {

    fun continueAssignment(id: UUID): CA
}
