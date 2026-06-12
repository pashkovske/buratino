package ru.pashkovske.buratino.assignment.service.core.refresh

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import java.util.UUID

abstract class ParentAssignmentRefresher<
    ChildA : Assignment,
    ChildCmd: AssignmentStartCmd<ChildA>,
    ParentA : ParentAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ParentA>,
    protected val childAssignmentDao: AssignmentDao<ChildA>,
    protected val childExe: AssignmentExe<ChildA, ChildCmd>
) : BasicAssignmentRefresher<ParentA>(
    assignmentDao = assignmentDao
) {

    override fun preRefresh(id: UUID): ExeCtx<ParentA> {
        val ctx: ExeCtx<ParentA> = super.preRefresh(id)
        syncChild(ctx)
        return ctx
    }

    protected fun syncChild(ctx: ExeCtx<ParentA>) {
        ctx.assignment.child = childAssignmentDao.get(ctx.assignment.child.id)
    }

    protected fun isChildCompleted(ctx: ExeCtx<ParentA>): Boolean {
        return ctx.assignment.child.state == AssignmentState.COMPLETED
    }

    protected fun refreshChild(ctx: ExeCtx<ParentA>) {
        val assignment: ParentA = ctx.assignment
        assignment.child = childExe.refresh(assignment.child.id)
        ctx.setMutated()
    }
}