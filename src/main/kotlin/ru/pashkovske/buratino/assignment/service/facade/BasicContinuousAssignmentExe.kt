package ru.pashkovske.buratino.assignment.service.facade

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.service.core.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousStartCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousA>,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousA, ChildA>,
    assignmentRefresher: AssignmentRefresher<ContinuousA>,
    assignmentCanceller: AssignmentCanceller<ContinuousA>,
    assignmentStarter: AssignmentStarter<ContinuousA>,
    assignmentBuilder: ContinuousAssignmentBuilder<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>,
    childAssignmentDao: AssignmentDao<ChildA>,
    protected val continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousA>
):
    ParentAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd, ChildCmd>(
        assignmentDao = assignmentDao,
        refreshNotifyOrchestrator = refreshNotifyOrchestrator,
        assignmentRefresher = assignmentRefresher,
        assignmentCanceller = assignmentCanceller,
        assignmentStarter = assignmentStarter,
        assignmentBuilder = assignmentBuilder,
        childAssignmentDao = childAssignmentDao
    ),
    ContinuousAssignmentExe<ContinuousA, ChildA, ContinuousStartCmd>
{

    @PostConstruct
    override fun recoverAssignments(): List<ContinuousA> {
        val assignmentsToRecover: List<ContinuousA> = super.recoverAssignments()

        assignmentsToRecover.filter { assignment: ContinuousA ->
            assignment.continueAssignmentSchedulingProperties != null
        }.forEach { assignment: ContinuousA ->
            recoverContinueNotifier(assignment)
            assignmentDao.update(assignment)
            continueAssignment(assignment.id)
        }
        return assignmentsToRecover
    }

    final override fun continueAssignment(id: UUID): ContinuousA {
        return continuousAssignmentContinuer.continueAssignment(id)
    }

    private fun recoverContinueNotifier(assignment: ContinuousA) {
        val notifier: AssignmentScheduling? = continueNotifyOrchestrator.build(
            properties = assignment.continueAssignmentSchedulingProperties,
            assignmentId = assignment.id
        )
        val notifierId: UUID? = continueNotifyOrchestrator.register(notifier)
        continueNotifyOrchestrator.start(notifierId)
        if (notifierId != null) {
            assignment.clearContinueScheduling()
            val notifier: AssignmentScheduling = continueNotifyOrchestrator.get(notifierId)!!
            assignment.initContinueScheduling(notifier)
        }
    }
}
