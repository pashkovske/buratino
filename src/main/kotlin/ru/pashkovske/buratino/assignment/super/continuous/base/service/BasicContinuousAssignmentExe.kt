package ru.pashkovske.buratino.assignment.`super`.continuous.base.service

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.SchedulingAssignmentTask
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.`super`.base.service.BasicSuperAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    assignmentScheduler: AssignmentTaskScheduler,
    nestedAssignmentExe: AssignmentExe<Nested>,
    nestedAssignmentDao: AssignmentDao<Nested>
):
    BasicSuperAssignmentExe<ContinuousA, Nested>(
        assignmentDao = assignmentDao,
        nestedAssignmentExe = nestedAssignmentExe,
        assignmentScheduler = assignmentScheduler,
        nestedAssignmentDao = nestedAssignmentDao
    ),
    ContinuousAssignmentExe<ContinuousA>
{

    private val log = KotlinLogging.logger {}

    @PostConstruct
    override fun recoverAssignments(): List<ContinuousA> {
        val assignmentsToRecover: List<ContinuousA> = super.recoverAssignments()

        assignmentsToRecover.filter { assignment: ContinuousA ->
            assignment.continueSchedulingProperties != null
        }.forEach { assignment: ContinuousA ->
            assignment.clearContinueScheduling()
            scheduleContinue(assignment)
            assignmentDao.update(assignment)
            continueAssignment(assignment.id)
        }
        return assignmentsToRecover
    }

    final override fun continueAssignment(id: UUID): ContinuousA {
        val ctx: ExeCtx<ContinuousA> = preContinue(id)
        if (!ctx.shouldSkip()) {
            doContinue(ctx)
        }
        postContinue(ctx)
        return ctx.assignment
    }
    protected open fun preContinue(id: UUID): ExeCtx<ContinuousA> {
        val assignment: ContinuousA = assignmentDao.get(id)
        log.info("Continuing assignment: $assignment")
        val ctx: ExeCtx<ContinuousA> = ExeCtx(assignment)
        syncNested(ctx)
        if (isCompleted(ctx)) {
            log.warn("Assignment ${ctx.assignment.id} is already completed. Skipping continue")
            ctx.setShouldSkip()
        }
        return ctx
    }
    protected abstract fun doContinue(ctx: ExeCtx<ContinuousA>)
    protected open fun postContinue(ctx: ExeCtx<ContinuousA>) {
        if (ctx.isMutated()) {
            assignmentDao.update(ctx.assignment)
        }
        log.info("Assignment continued: ${ctx.assignment}")
    }

    override fun postStart(ctx: ExeCtx<ContinuousA>) {
        scheduleContinue(ctx)
        super.postStart(ctx)
    }

    override fun postCancel(ctx: ExeCtx<ContinuousA>) {
        stopSchedulingContinuation(ctx)
        super.postCancel(ctx)
    }

    private fun scheduleContinue(ctx: ExeCtx<ContinuousA>) {
        val assignment: ContinuousA = ctx.assignment
        val scheduled: Boolean = scheduleContinue(assignment)
        if (scheduled) {
            ctx.setMutated()
        }
    }

    private fun scheduleContinue(assignment: ContinuousA): Boolean {
        val schedulingProps: SchedulingProperties = assignment.continueSchedulingProperties ?: return false

        val task = SchedulingAssignmentTask(
            action = this::continueAssignment,
            assignmentId = assignment.id
        )
        val schedulingInfo = SchedulingInfo(
            properties = schedulingProps,
            taskId = UUID.randomUUID()
        )
        assignmentScheduler.start(
            task = task,
            taskId = schedulingInfo.taskId,
            interval = schedulingProps.interval
        )
        schedulingInfo.status = SchedulingStatus.ACTIVE
        assignment.initContinueScheduling(schedulingInfo)
        return true
    }

    private fun stopSchedulingContinuation(ctx: ExeCtx<ContinuousA>) {
        val schedulingInfo: SchedulingInfo = ctx.assignment.getContinueSchedulingInfo() ?: return

        assignmentScheduler.stop(schedulingInfo.taskId)
        schedulingInfo.status = SchedulingStatus.COMPLETED

        ctx.setMutated()
    }
}
