package ru.pashkovske.buratino.assignment.service.core.start

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.service.core.AssignmentStateMachine
import java.util.UUID

abstract class BasicAssignmentStarter<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>
) : AssignmentStarter<A> {

    private val log: KLogger = KotlinLogging.logger {}

    final override fun start(id: UUID): A {
        val ctx: ExeCtx<A> = preStart(id)
        if (!ctx.shouldSkip()) {
            doStart(ctx)
        }
        postStart(ctx)
        return ctx.assignment
    }

    protected open fun preStart(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentDao.get(id)
        log.info("Starting assignment: $assignment")
        val ctx: ExeCtx<A> = ExeCtx(assignment)
        return ctx
    }

    protected abstract fun doStart(ctx: ExeCtx<A>)

    protected open fun postStart(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        toInProgress(ctx)
        assignmentDao.update(assignment)
        log.info("Assignment started: $assignment")
    }

    protected fun toInProgress(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }
}
