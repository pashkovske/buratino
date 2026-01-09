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
        val ctx = preStart(assignment)
        doStart(ctx)
        postStart(ctx)
        return assignment
    }
    protected open fun preStart(assignment: A): ExeCtx<A> {
        log.info("Starting assignment: $assignment")
        return ExeCtx(assignment)
    }
    protected abstract fun doStart(ctx: ExeCtx<A>)
    protected open fun postStart(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        assignmentRepo.create(assignment)
        log.info("Assignment started: $assignment")
    }

    final override fun refresh(id: UUID): A {
        val ctx = preRefresh(id)
        doRefresh(ctx)
        postRefresh(ctx)
        return ctx.assignment
    }
    protected open fun preRefresh(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
        log.info("Refreshing assignment: $assignment")
        return ExeCtx(assignment)
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
        doCancel(ctx)
        postCancel(ctx)
        return ctx.assignment
    }
    protected open fun preCancel(id: UUID): ExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
        log.info("Canceling assignment: $assignment")
        return ExeCtx(assignment)
    }
    protected abstract fun doCancel(ctx: ExeCtx<A>)
    protected open fun postCancel(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        stopSchedulingRefresh(ctx)
        if (ctx.isMutated()) {
            assignmentRepo.update(assignment)
        }
        log.info("Assignment canceled: $assignment")
    }

    protected fun setStatusInProgress(assignment: A) {
        assignment.status = AssignmentStatus.IN_PROGRESS
    }
    protected fun setStatusCompleted(assignment: A) {
        assignment.status = AssignmentStatus.COMPLETED
    }
    protected fun checkCompleted(assignment: A): Boolean {
        return assignment.status == AssignmentStatus.COMPLETED
    }

    protected fun scheduleRefresh(ctx: ExeCtx<A>) {
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
