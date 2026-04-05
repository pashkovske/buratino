package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class ContinuousAssignmentBuilder<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    childBuilder: AssignmentBuilder<ChildA, ChildCmd>,
    assignmentDao: AssignmentDao<ContinuousA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator
) : ParentAssignmentBuilder<ContinuousA, ChildA, ContinuousCmd, ChildCmd>(
    childBuilder = childBuilder,
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    override fun postBuildParent(assignment: ContinuousA, cmd: ContinuousCmd) {
        val notifier: AssignmentNotifier? = continueNotifyOrchestrator.build(
            properties = cmd.continueNotifierProperties,
            assignmentId = assignment.id
        )
        val notifierId: UUID? = continueNotifyOrchestrator.register(notifier)
        assignment.initContinueNotifierId(notifierId)
    }
}