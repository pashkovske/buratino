package ru.pashkovske.buratino.assignment.service.core.refresh

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.ExeCtx
import java.util.UUID

abstract class BasicAssignmentRefresher<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>
) : AssignmentRefresher<A> {

    private val log: KLogger = KotlinLogging.logger {}

    final override fun refresh(id: UUID): A {
        val ctx: ExeCtx<A> = preRefresh(id)
        if (!ctx.shouldSkip()) {
            doRefresh(ctx)
        }
        postRefresh(ctx)
        return ctx.assignment
    }
    protected open fun preRefresh(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentDao.get(id)
        log.info("Refreshing assignment: $assignment")
        val ctx: ExeCtx<A> = ExeCtx(assignment)
        if (isCompleted(ctx)) {
            log.warn("Assignment ${ctx.assignment.id} is already completed. Skipping refresh")
            ctx.setShouldSkip()
        }
        return ctx
    }
    protected abstract fun doRefresh(ctx: ExeCtx<A>)
    protected open fun postRefresh(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        if (ctx.isMutated()) {
            assignmentDao.update(assignment)
        }
        log.info("Assignment refreshed: $assignment")
    }

    protected fun isCompleted(ctx: ExeCtx<A>): Boolean {
        return ctx.assignment.state == AssignmentState.COMPLETED
    }
}