package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ParentAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

abstract class ParentAssignmentBuilder<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ParentCmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    protected val childExe: AssignmentExe<ChildA, ChildCmd>,
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : BasicAssignmentBuilder<ParentA, ParentCmd>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    protected abstract fun buildChildStartCmd(cmd: ParentCmd): ChildCmd

    protected abstract fun preBuildParent(cmd: ParentCmd, child: ChildA): ParentA

    protected open fun postBuildParent(assignment: ParentA, cmd: ParentCmd) {
    }

    final override fun preBuild(cmd: ParentCmd): ParentA {
        val childStartCmd: ChildCmd = buildChildStartCmd(cmd)
        val childAssignment: ChildA = childExe.build(childStartCmd)
        val parentAssignment: ParentA = preBuildParent(cmd, childAssignment)
        postBuildParent(parentAssignment, cmd)
        return parentAssignment
    }
}