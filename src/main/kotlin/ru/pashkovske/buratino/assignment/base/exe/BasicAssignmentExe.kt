package ru.pashkovske.buratino.assignment.base.exe

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.SchedulingAssignmentTask
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import java.util.UUID

abstract class BasicAssignmentExe<A: Assignment>(
    protected val assignmentRepo: AssignmentRepo<A>,
    protected val assignmentScheduler: AssignmentTaskScheduler
): AssignmentExe<A> {

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
        scheduleRefresh(ctx)
        toInProgress(ctx)
        assignmentRepo.create(assignment)
        log.info("Assignment started: $assignment")
    }

    final override fun refresh(id: UUID): A {
        val ctx: ExeCtx<A> = preRefresh(id)
        if (!ctx.shouldSkip()) {
            doRefresh(ctx)
        }
        postRefresh(ctx)
        return ctx.assignment
    }
    protected open fun preRefresh(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
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
            assignmentRepo.update(assignment)
        }
        log.info("Assignment refreshed: $assignment")
    }

    final override fun cancel(id: UUID): A {
        val ctx: ExeCtx<A> = preCancel(id)
        if (!ctx.shouldSkip()) {
            doCancel(ctx)
        }
        postCancel(ctx)
        return ctx.assignment
    }
    protected open fun preCancel(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
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
            assignmentRepo.update(assignment)
        }
        log.info("Assignment canceled: $assignment")
    }

    protected fun toInProgress(ctx: ExeCtx<A>) {
        StatusStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }
    protected fun toCompleted(ctx: ExeCtx<A>) {
        StatusStateMachine.toCompleted(ctx.assignment)
        ctx.setMutated()
    }
    protected fun isCompleted(ctx: ExeCtx<A>): Boolean {
        return ctx.assignment.status == AssignmentStatus.COMPLETED
    }

    private fun scheduleRefresh(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        val schedulingProps: SchedulingProperties = assignment.refreshSchedulingProperties ?: return

        val task = SchedulingAssignmentTask(
            action = this::refresh,
            assignmentId = assignment.id
        )
        val schedulingInfo = SchedulingInfo(
            properties = schedulingProps,
            taskId = UUID.randomUUID(),
            status = SchedulingStatus.QUEUED
        )
        assignmentScheduler.start(
            task = task,
            taskId = schedulingInfo.taskId,
            interval = schedulingProps.interval
        )
        schedulingInfo.status = SchedulingStatus.ACTIVE
        assignment.initRefreshScheduling(schedulingInfo)

        ctx.setMutated()
    }

    private fun stopSchedulingRefresh(ctx: ExeCtx<A>) {
        val schedulingInfo: SchedulingInfo = ctx.assignment.getRefreshSchedulingInfo() ?: return
        if (schedulingInfo.status == SchedulingStatus.COMPLETED) {
            return
        }
        assignmentScheduler.stop(schedulingInfo.taskId)
        schedulingInfo.status = SchedulingStatus.COMPLETED

        ctx.setMutated()
    }
}
