package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

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

    override fun postBuildParent(assignment: ContinuousA) {
        val notifier: AssignmentScheduling? = continueNotifyOrchestrator.build(
            properties = assignment.continueAssignmentSchedulingProperties,
            assignmentId = assignment.id
        )
        continueNotifyOrchestrator.register(notifier)
        if (notifier != null) {
            assignment.initContinueScheduling(notifier)
        }
    }
}