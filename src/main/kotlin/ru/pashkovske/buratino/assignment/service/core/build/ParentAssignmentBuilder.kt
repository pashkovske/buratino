package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ParentAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

abstract class ParentAssignmentBuilder<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ParentCmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    protected val childBuilder: AssignmentBuilder<ChildA, ChildCmd>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ParentA>
) : BasicAssignmentBuilder<ParentA, ParentCmd>(
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    protected abstract fun buildChildStartCmd(cmd: ParentCmd): ChildCmd

    protected abstract fun preBuildParent(cmd: ParentCmd, child: ChildA): ParentA

    protected abstract fun postBuildParent(assignment: ParentA)

    final override fun preBuild(cmd: ParentCmd): ParentA {
        val childStartCmd: ChildCmd = buildChildStartCmd(cmd)
        val childAssignment: ChildA = childBuilder.build(childStartCmd)
        val parentAssignment: ParentA = preBuildParent(cmd, childAssignment)
        postBuildParent(parentAssignment)
        return parentAssignment
    }
}