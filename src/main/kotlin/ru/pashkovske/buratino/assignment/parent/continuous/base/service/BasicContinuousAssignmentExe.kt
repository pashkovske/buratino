package ru.pashkovske.buratino.assignment.parent.continuous.base.service

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.parent.base.service.ParentAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
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
