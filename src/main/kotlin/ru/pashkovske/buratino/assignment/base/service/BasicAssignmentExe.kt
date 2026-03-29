package ru.pashkovske.buratino.assignment.base.service

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import java.util.UUID

abstract class BasicAssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    >(
    protected val assignmentDao: AssignmentDao<A>,
    private val refreshNotifyOrchestrator: RefreshNotifyOrchestrator<A>,
    protected val assignmentRefresher: AssignmentRefresher<A>,
    protected val assignmentCanceller: AssignmentCanceller<A>,
    protected val assignmentStarter: AssignmentStarter<A>,
    protected val assignmentBuilder: AssignmentBuilder<A, Cmd>
) : AssignmentExe<A, Cmd> {

    @PostConstruct
    override fun recoverAssignments(): List<A> {
        val activeAssignments: List<A> = assignmentDao.getByState(AssignmentState.IN_PROGRESS)
        activeAssignments.forEach { assignment: A ->
            recoverRefreshNotifier(assignment)
            assignmentDao.update(assignment)
            refresh(assignment.id)
        }
        return activeAssignments
    }

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

    private fun recoverRefreshNotifier(assignment: A) {
        val notifier: AssignmentScheduling? = refreshNotifyOrchestrator.build(
            properties = assignment.refreshAssignmentSchedulingProperties,
            assignmentId = assignment.id
        )
        val notifierId: UUID? = refreshNotifyOrchestrator.register(notifier)
        refreshNotifyOrchestrator.start(notifierId)
        if (notifierId != null) {
            assignment.clearRefreshScheduling()
            val notifier: AssignmentScheduling = refreshNotifyOrchestrator.get(notifierId)!!
            assignment.initRefreshScheduling(notifier)
        }
    }
}
