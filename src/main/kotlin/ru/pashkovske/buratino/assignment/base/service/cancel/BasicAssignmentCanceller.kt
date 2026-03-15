package ru.pashkovske.buratino.assignment.base.service.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.service.AssignmentStateMachine
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicAssignmentCanceller<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>,
    private val taskScheduler: TaskScheduler
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
        stopSchedulingRefresh(ctx)
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

    private fun stopSchedulingRefresh(ctx: ExeCtx<A>) {
        val assignmentScheduling: AssignmentScheduling = ctx.assignment.getRefreshAssignmentScheduling() ?: return
        if (assignmentScheduling.state == SchedulingState.COMPLETED) {
            return
        }
        taskScheduler.stopPeriodic(assignmentScheduling.taskId)
        assignmentScheduling.state = SchedulingState.COMPLETED

        ctx.setMutated()
    }
}
