package ru.pashkovske.buratino.assignment.service.core.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.service.core.AssignmentStateMachine
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class BasicAssignmentCanceller<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>,
    private val refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : AssignmentCanceller<A> {

    private val log: KLogger = KotlinLogging.logger {}

    final override fun cancel(id: UUID): A {
        val ctx: ExeCtx<A> = preCancel(id)
        if (!ctx.shouldSkip()) {
            doCancel(ctx)
        }
        postCancel(ctx)
        return ctx.assignment
    }
    protected open fun preCancel(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentDao.get(id)
        log.info("Canceling assignment: $assignment")
        val ctx: ExeCtx<A> = ExeCtx(assignment)
        if (isCompleted(ctx)) {
            log.warn("Assignment ${ctx.assignment.id} is already completed. Skipping cancel")
            ctx.setShouldSkip()
        }
        return ctx
    }
    protected abstract fun doCancel(ctx: ExeCtx<A>)
    protected open fun postCancel(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        stopRefreshNotifier(ctx)
        toCompleted(ctx)
        if (ctx.isMutated()) {
            assignmentDao.update(assignment)
        }
        log.info("Assignment canceled: $assignment")
    }

    protected fun toCompleted(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toCompleted(ctx.assignment)
        ctx.setMutated()
    }
    protected fun isCompleted(ctx: ExeCtx<A>): Boolean {
        return ctx.assignment.state == AssignmentState.COMPLETED
    }

    private fun stopRefreshNotifier(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        val stopped: List<UUID> = refreshNotifyOrchestrator.stopForAssignment(assignment.id)
        if (stopped.isNotEmpty()) {
            assignment.clearRefreshScheduling()
            assignment.initRefreshScheduling(refreshNotifyOrchestrator.get(stopped.first())!!)
            ctx.setMutated()
        }
    }
}
