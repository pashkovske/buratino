package ru.pashkovske.buratino.assignment.parent.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignmentStartCmd
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class ParentAssignmentStarter<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    Cmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ParentA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ParentA>,
    protected val childAssignmentExe: AssignmentExe<ChildA, ChildCmd>
) : BasicAssignmentStarter<ParentA, Cmd>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher
) {

    protected abstract fun buildChildStartCmd(cmd: Cmd): ChildCmd

    final override fun buildAssignment(cmd: Cmd): ParentA {
        val childStartCmd = buildChildStartCmd(cmd)
        val childAssignment = childAssignmentExe.start(childStartCmd)
        return buildParentAssignment(cmd, childAssignment)
    }

    protected abstract fun buildParentAssignment(cmd: Cmd, child: ChildA): ParentA

    override fun doStart(ctx: ExeCtx<ParentA>) {
        // Child is already started in buildAssignment, nothing to do here
    }
}
