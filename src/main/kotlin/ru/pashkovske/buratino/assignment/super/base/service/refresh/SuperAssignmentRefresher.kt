package ru.pashkovske.buratino.assignment.`super`.base.service.refresh

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.refresh.BasicAssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import java.util.UUID

abstract class SuperAssignmentRefresher<
    NestedA : Assignment,
    SuperA : SuperAssignment<NestedA>
    >(
    assignmentDao: AssignmentDao<SuperA>,
    protected val nestedAssignmentDao: AssignmentDao<NestedA>,
    protected val nestedAssignmentRefresher: AssignmentRefresher<NestedA>
) : BasicAssignmentRefresher<SuperA>(
    assignmentDao = assignmentDao
) {

    override fun preRefresh(id: UUID): ExeCtx<SuperA> {
        val ctx: ExeCtx<SuperA> = super.preRefresh(id)
        syncNested(ctx)
        return ctx
    }

    protected fun syncNested(ctx: ExeCtx<SuperA>) {
        ctx.assignment.nested = nestedAssignmentDao.get(ctx.assignment.nested.id)
    }

    protected fun isNestedCompleted(ctx: ExeCtx<SuperA>): Boolean {
        return ctx.assignment.nested.state == AssignmentState.COMPLETED
    }

    protected fun refreshNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        assignment.nested = nestedAssignmentRefresher.refresh(assignment.nested.id)
        ctx.setMutated()
    }
}
