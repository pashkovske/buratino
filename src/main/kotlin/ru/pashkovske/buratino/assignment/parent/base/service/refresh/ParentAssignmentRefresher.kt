package ru.pashkovske.buratino.assignment.parent.base.service.refresh

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.refresh.BasicAssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import java.util.UUID

abstract class ParentAssignmentRefresher<
    ChildA : Assignment,
    ParentA : ParentAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ParentA>,
    protected val childAssignmentDao: AssignmentDao<ChildA>,
    protected val childAssignmentRefresher: AssignmentRefresher<ChildA>
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
        assignment.child = childAssignmentRefresher.refresh(assignment.child.id)
        ctx.setMutated()
    }
}
