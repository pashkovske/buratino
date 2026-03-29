package ru.pashkovske.buratino.assignment.parent.base.service.builder

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.build.BasicAssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignmentStartCmd

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
