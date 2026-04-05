package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import java.util.UUID

abstract class BasicAssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    >(
    protected val assignmentDao: AssignmentDao<A>,
    protected val assignmentRefresher: AssignmentRefresher<A>,
    protected val assignmentCanceller: AssignmentCanceller<A>,
    protected val assignmentStarter: AssignmentStarter<A>,
    protected val assignmentBuilder: AssignmentBuilder<A, Cmd>
) : AssignmentExe<A, Cmd> {

    final override fun start(cmd: Cmd): A {
        val assignment: A = assignmentBuilder.build(cmd)
        return assignmentStarter.start(assignment)
    }

    final override fun refresh(id: UUID): A {
        return assignmentRefresher.refresh(id)
    }

    final override fun cancel(id: UUID): A {
        return assignmentCanceller.cancel(id)
    }
}
