package ru.pashkovske.buratino.assignment.parent.continuous.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import java.util.UUID

interface ContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousStartCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>
    >: AssignmentExe<ContinuousA, ContinuousStartCmd> {

    fun continueAssignment(id: UUID): ContinuousA
}
