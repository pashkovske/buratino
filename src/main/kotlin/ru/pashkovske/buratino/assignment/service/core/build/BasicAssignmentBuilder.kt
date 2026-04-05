package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class BasicAssignmentBuilder<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>,
    >(
    private val refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    private val assignmentDao: AssignmentDao<A>
) : AssignmentBuilder<A, Cmd> {

    protected abstract fun preBuild(cmd: Cmd): A

    protected open fun postBuild(assignment: A) {
        assignmentDao.create(assignment)
    }

    final override fun build(cmd: Cmd): A {
        val assignment: A = preBuild(cmd)
        val notifier: AssignmentScheduling? = refreshNotifyOrchestrator.build(
            properties = cmd.refreshAssignmentSchedulingProperties,
            assignmentId = assignment.id
        )
        val notifierId: UUID? = refreshNotifyOrchestrator.register(notifier)
        assignment.initRefreshNotifierId(notifierId)
        postBuild(assignment)
        return assignment
    }
}
