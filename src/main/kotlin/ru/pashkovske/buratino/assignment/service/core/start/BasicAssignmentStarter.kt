package ru.pashkovske.buratino.assignment.service.core.start

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.service.core.AssignmentStateMachine
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import java.util.UUID

abstract class BasicAssignmentStarter<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>,
    private val refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : AssignmentStarter<A> {

    private val log: KLogger = KotlinLogging.logger {}

    final override fun start(assignment: A): A {
        val ctx: ExeCtx<A> = preStart(assignment)
        if (!ctx.shouldSkip()) {
            doStart(ctx)
        }
        postStart(ctx)
        return assignment
    }

    protected open fun preStart(assignment: A): ExeCtx<A> {
        log.info("Starting assignment: $assignment")
        val ctx: ExeCtx<A> = ExeCtx(assignment)
        return ctx
    }

    protected abstract fun doStart(ctx: ExeCtx<A>)

    protected open fun postStart(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        startRefreshNotifier(ctx)
        toInProgress(ctx)
        assignmentDao.update(assignment)
        log.info("Assignment started: $assignment")
    }

    protected fun toInProgress(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }

    private fun startRefreshNotifier(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        val started: List<UUID> = refreshNotifyOrchestrator.startForAssignment(assignment.id)
        if (started.isNotEmpty()) {
            assignment.clearRefreshScheduling()
            assignment.initRefreshScheduling(refreshNotifyOrchestrator.get(started.first())!!)
            ctx.setMutated()
        }
    }
}
