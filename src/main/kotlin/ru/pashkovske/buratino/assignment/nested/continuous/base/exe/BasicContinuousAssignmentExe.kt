package ru.pashkovske.buratino.assignment.nested.continuous.base.exe

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.SchedulingAssignmentTask
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.exe.AssignmentExe
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.nested.base.exe.BasicSuperAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import java.util.UUID

abstract class BasicContinuousAssignmentExe<
    ContinuousA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentRepo: AssignmentRepo<ContinuousA>,
    assignmentScheduler: AssignmentTaskScheduler,
    nestedAssignmentExe: AssignmentExe<Nested>,
    nestedAssignmentRepo: AssignmentRepo<Nested>
):
    BasicSuperAssignmentExe<ContinuousA, Nested>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe,
        assignmentScheduler = assignmentScheduler,
        nestedAssignmentRepo = nestedAssignmentRepo
    ),
    ContinuousAssignmentExe<ContinuousA>
{

    private val log = KotlinLogging.logger {}

    final override fun continueAssignment(id: UUID): ContinuousA {
        val ctx: ExeCtx<ContinuousA> = preContinue(id)
        if (!ctx.shouldSkip()) {
            doContinue(ctx)
        }
        postContinue(ctx)
        return ctx.assignment
    }
    protected open fun preContinue(id: UUID): ExeCtx<ContinuousA> {
        val assignment: ContinuousA = assignmentRepo.get(id)
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
            assignmentRepo.update(ctx.assignment)
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
        val schedulingProps: SchedulingProperties = assignment.continueSchedulingProperties ?: return

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

        ctx.setMutated()
    }

    private fun stopSchedulingContinuation(ctx: ExeCtx<ContinuousA>) {
        val schedulingInfo: SchedulingInfo = ctx.assignment.getContinueSchedulingInfo() ?: return

        assignmentScheduler.stop(schedulingInfo.taskId)
        schedulingInfo.status = SchedulingStatus.COMPLETED

        ctx.setMutated()
    }
}
