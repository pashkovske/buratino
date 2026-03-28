package ru.pashkovske.buratino.assignment.base.service.start

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.service.AssignmentStateMachine
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicAssignmentStarter<A : Assignment>(
    private val assignmentDao: AssignmentDao<A>,
    private val taskScheduler: TaskScheduler,
    private val assignmentRefresher: AssignmentRefresher<A>
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
        scheduleRefresh(ctx)
        toInProgress(ctx)
        assignmentDao.create(assignment)
        log.info("Assignment started: $assignment")
    }

    protected fun toInProgress(ctx: ExeCtx<A>) {
        AssignmentStateMachine.toInProgress(ctx.assignment)
        ctx.setMutated()
    }

    private fun scheduleRefresh(ctx: ExeCtx<A>) {
        val assignment: A = ctx.assignment
        scheduleRefresh(assignment)
        ctx.setMutated()
    }

    private fun scheduleRefresh(assignment: A) {
        val schedulingProps: AssignmentSchedulingProperties = assignment.refreshAssignmentSchedulingProperties ?: return
        if (schedulingProps !is PeriodicAssignmentSchedulingProperties) {
            return
        }

        val subscriber = AssignmentSchedulingSubscriber(
            action = assignmentRefresher::refresh,
            assignmentId = assignment.id
        )
        val taskId: UUID = taskScheduler.startNewPeriodic(schedulingProps.period)
        taskScheduler.subscribePeriodic(taskId, subscriber)
        val assignmentScheduling = AssignmentScheduling(
            id = UUID.randomUUID(),
            assignmentId = assignment.id,
            properties = schedulingProps,
            taskId = taskId,
            state = SchedulingState.ACTIVE
        )
        assignmentScheduling.state = SchedulingState.ACTIVE
        assignment.initRefreshScheduling(assignmentScheduling)
    }
}
