package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import java.util.UUID

interface ContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousStartCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>
    >: AssignmentExe<ContinuousA, ContinuousStartCmd> {

    fun continueAssignment(id: UUID): ContinuousA
}
