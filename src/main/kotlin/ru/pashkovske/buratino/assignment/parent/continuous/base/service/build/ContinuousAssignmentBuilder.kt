package ru.pashkovske.buratino.assignment.parent.continuous.base.service.build

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.base.service.builder.ParentAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator

abstract class ContinuousAssignmentBuilder<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    childBuilder: AssignmentBuilder<ChildA, ChildCmd>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousA>,
    private val continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousA, ChildA>
) : ParentAssignmentBuilder<ContinuousA, ChildA, ContinuousCmd, ChildCmd>(
    childBuilder = childBuilder,
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
