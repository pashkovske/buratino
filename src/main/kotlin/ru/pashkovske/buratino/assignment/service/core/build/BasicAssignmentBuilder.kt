package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

abstract class BasicAssignmentBuilder<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    >(
        private val refreshNotifyOrchestrator: RefreshNotifyOrchestrator<A>
    ) : AssignmentBuilder<A, Cmd> {

    protected abstract fun preBuild(cmd: Cmd): A

    protected open fun postBuild(assignment: A) {
        val notifier: AssignmentScheduling? = refreshNotifyOrchestrator.build(
            properties = assignment.refreshAssignmentSchedulingProperties,
            assignmentId = assignment.id
        )
        refreshNotifyOrchestrator.register(notifier)
        if (notifier != null) {
            assignment.initRefreshScheduling(notifier)
        }
    }

    final override fun build(cmd: Cmd): A {
        val assignment: A = preBuild(cmd)
        postBuild(assignment)
        return assignment
    }
}
