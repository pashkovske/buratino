package ru.pashkovske.buratino.assignment.base.service

import jakarta.annotation.PostConstruct
import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicAssignmentExe<A: Assignment>(
    protected val assignmentDao: AssignmentDao<A>,
    protected val taskScheduler: TaskScheduler
): AssignmentExe<A> {

    private val log: KLogger = KotlinLogging.logger {}

    @PostConstruct
    override fun recoverAssignments(): List<A> {
        val activeAssignments: List<A> = assignmentDao.getByStatus(AssignmentState.IN_PROGRESS)
        activeAssignments.filter { assignment: A ->
            assignment.refreshSchedulingProperties != null
        }.forEach { assignment: A ->
            assignment.clearRefreshScheduling()
            scheduleRefresh(assignment)
            assignmentDao.update(assignment)
            refresh(assignment.id)
        }
        return activeAssignments
    }

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
        assignmentDao.create(assignment)
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

    protected fun toInProgress(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }
    protected fun toCompleted(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toCompleted(ctx.assignment)
        ctx.setMutated()
    }
    protected fun isCompleted(ctx: ExeCtx<A>): Boolean {
        return ctx.assignment.status == AssignmentState.COMPLETED
    }

    private fun scheduleRefresh(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        scheduleRefresh(assignment)
        ctx.setMutated()
    }

    private fun scheduleRefresh(assignment: A) {
        val schedulingProps: SchedulingProperties = assignment.refreshSchedulingProperties ?: return

        val subscriber = AssignmentSchedulingSubscriber(
            action = this::refresh,
            assignmentId = assignment.id
        )
        val taskId: UUID = taskScheduler.startNewPeriodic(
            period = schedulingProps.interval,
            subscriber = subscriber
        )
        val assignmentScheduling = AssignmentScheduling(
            properties = schedulingProps,
            taskId = taskId,
            status = SchedulingState.ACTIVE
        )
        assignmentScheduling.status = SchedulingState.ACTIVE
        assignment.initRefreshScheduling(assignmentScheduling)
    }

    private fun stopSchedulingRefresh(ctx: ExeCtx<A>) {
        val assignmentScheduling: AssignmentScheduling = ctx.assignment.getRefreshSchedulingInfo() ?: return
        if (assignmentScheduling.status == SchedulingState.COMPLETED) {
            return
        }
        taskScheduler.stopPeriodic(assignmentScheduling.taskId)
        assignmentScheduling.status = SchedulingState.COMPLETED

        ctx.setMutated()
    }
}
