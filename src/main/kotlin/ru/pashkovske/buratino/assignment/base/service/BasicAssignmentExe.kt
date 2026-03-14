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
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicAssignmentExe<A: Assignment>(
    protected val assignmentDao: AssignmentDao<A>,
    protected val taskScheduler: TaskScheduler,
    protected val assignmentRefresher: AssignmentRefresher<A>,
    protected val assignmentCanceller: AssignmentCanceller<A>
): AssignmentExe<A> {

    private val log: KLogger = KotlinLogging.logger {}

    @PostConstruct
    override fun recoverAssignments(): List<A> {
        val activeAssignments: List<A> = assignmentDao.getByState(AssignmentState.IN_PROGRESS)
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
        return assignmentRefresher.refresh(id)
    }

    final override fun cancel(id: UUID): A {
        return assignmentCanceller.cancel(id)
    }

    protected fun toInProgress(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }
    protected fun isCompleted(ctx: ExeCtx<A>): Boolean {
        return ctx.assignment.state == AssignmentState.COMPLETED
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
            state = SchedulingState.ACTIVE
        )
        assignmentScheduling.state = SchedulingState.ACTIVE
        assignment.initRefreshScheduling(assignmentScheduling)
    }

}
