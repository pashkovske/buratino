package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator
import java.util.UUID

abstract class BasicAssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    >(
    protected val assignmentRefresher: AssignmentRefresher<A>,
    protected val assignmentCanceller: AssignmentCanceller<A>,
    protected val assignmentStarter: AssignmentStarter<A>,
    protected val assignmentBuilder: AssignmentBuilder<A, Cmd>,
    protected val refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : AssignmentExe<A, Cmd> {

    final override fun build(cmd: Cmd): A {
        return assignmentBuilder.build(cmd)
    }

    final override fun start(id: UUID): A {
        val assignment: A = assignmentStarter.start(id)
        startAutorefresh(assignment)
        return assignment
    }

    final override fun refresh(id: UUID): A {
        return assignmentRefresher.refresh(id)
    }

    final override fun cancel(id: UUID): A {
        val assignment: A = assignmentCanceller.cancel(id)
        stopAutorefresh(assignment)
        return assignment
    }

    private fun startAutorefresh(assignment: A) {
        refreshNotifyOrchestrator.start(
            id = assignment.getRefreshNotifierId(),
            subscriber = AssignmentNotifierSubscriber(
                action = { assignmentId: UUID -> assignmentRefresher.refresh(assignmentId) },
                assignmentId = assignment.id
            )
        )
    }

    private fun stopAutorefresh(assignment: A) {
        refreshNotifyOrchestrator.stop(assignment.getRefreshNotifierId())
    }
}
