package ru.pashkovske.buratino.assignment.base.exe

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentCommandExeCtx
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
    protected open fun preStart(assignment: A): AssignmentCommandExeCtx<A> {
        log.info("Starting assignment: $assignment")
        return AssignmentCommandExeCtx(assignment)
    }
    protected abstract fun doStart(ctx: AssignmentCommandExeCtx<A>)
    protected open fun postStart(ctx: AssignmentCommandExeCtx<A>) {
        val assignment: A = ctx.assignment
        if (ctx.isMutated()) {
            assignmentRepo.create(assignment)
        }
        log.info("Assignment started: $assignment")
    }

    final override fun refresh(id: UUID): A {
        val ctx = preRefresh(id)
        doRefresh(ctx)
        postRefresh(ctx)
        return ctx.assignment
    }
    protected open fun preRefresh(id: UUID): AssignmentCommandExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
        log.info("Refreshing assignment: $assignment")
        return AssignmentCommandExeCtx(assignment)
    }
    protected abstract fun doRefresh(ctx: AssignmentCommandExeCtx<A>)
    protected open fun postRefresh(ctx: AssignmentCommandExeCtx<A>) {
        val assignment: A = ctx.assignment
        if (ctx.isMutated()) {
            assignmentRepo.update(assignment)
        }
        log.info("Assignment refreshed: $assignment")
    }

    final override fun cancel(id: UUID): A {
        val ctx: AssignmentCommandExeCtx<A> = preCancel(id)
        doCancel(ctx)
        postCancel(ctx)
        return ctx.assignment
    }
    protected open fun preCancel(id: UUID): AssignmentCommandExeCtx<A> {
        val assignment: A = assignmentRepo.get(id)
        log.info("Canceling assignment: $assignment")
        return AssignmentCommandExeCtx(assignment)
    }
    protected abstract fun doCancel(ctx: AssignmentCommandExeCtx<A>)
    protected open fun postCancel(ctx: AssignmentCommandExeCtx<A>) {
        val assignment: A = ctx.assignment
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

    protected fun scheduleRefresh(assignment: A) {
        val schedulingProps: SchedulingProperties? = assignment.refreshSchedulingProperties
        if (schedulingProps != null) {
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
        }
    }

    protected fun stopSchedulingRefresh(assignment: A) {
        val schedulingInfo: SchedulingInfo? = assignment.getRefreshSchedulingInfo()
        if (schedulingInfo != null) {
            assignmentScheduler.stop(schedulingInfo.taskId)
            schedulingInfo.status = SchedulingStatus.COMPLETED
        }
    }
}
