package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.service.core.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousStartCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    assignmentCanceller: AssignmentCanceller<ContinuousA>,
    assignmentStarter: AssignmentStarter<ContinuousA>,
    assignmentBuilder: ContinuousAssignmentBuilder<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>,
    childAssignmentDao: AssignmentDao<ChildA>,
    protected val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
) :
    ParentAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>(
        assignmentDao = assignmentDao,
        assignmentRefresher = assignmentRefresher,
        assignmentCanceller = assignmentCanceller,
        assignmentStarter = assignmentStarter,
        assignmentBuilder = assignmentBuilder,
        childAssignmentDao = childAssignmentDao
    ),
    ContinuousAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd> {

    final override fun continueAssignment(id: UUID): ContinuousA {
        return continuousAssignmentContinuer.continueAssignment(id)
    }
}
