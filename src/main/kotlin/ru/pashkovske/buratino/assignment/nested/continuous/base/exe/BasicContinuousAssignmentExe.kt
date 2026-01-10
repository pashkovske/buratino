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
    CA : ContinuousAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentRepo: AssignmentRepo<CA>,
    assignmentScheduler: AssignmentTaskScheduler,
    nestedAssignmentExe: AssignmentExe<Nested>
):
    BasicSuperAssignmentExe<CA, Nested>(
        assignmentRepo = assignmentRepo,
        nestedAssignmentExe = nestedAssignmentExe,
        assignmentScheduler = assignmentScheduler
    ),
    ContinuousAssignmentExe<CA>
{

    private val log = KotlinLogging.logger {}

    final override fun continueAssignment(id: UUID): CA {
        val ctx: ExeCtx<CA> = preContinue(id)
        doContinue(ctx)
        postContinue(ctx)
        return ctx.assignment
    }
    protected open fun preContinue(id: UUID): ExeCtx<CA> {
        val assignment: CA = assignmentRepo.get(id)
        log.info("Continuing assignment: $assignment")
        return ExeCtx(assignment)
    }
    protected abstract fun doContinue(ctx: ExeCtx<CA>)
    protected open fun postContinue(ctx: ExeCtx<CA>) {
        stopSchedulingContinuation(ctx)
        if (ctx.isMutated()) {
            assignmentRepo.update(ctx.assignment)
        }
        log.info("Assignment continued: ${ctx.assignment}")
    }

    override fun postCancel(ctx: ExeCtx<CA>) {
        stopSchedulingContinuation(ctx)
        super.postCancel(ctx)
    }

    protected fun scheduleContinue(ctx: ExeCtx<CA>) {
        val assignment: CA = ctx.assignment
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

    private fun stopSchedulingContinuation(ctx: ExeCtx<CA>) {
        val schedulingInfo: SchedulingInfo = ctx.assignment.getContinueSchedulingInfo() ?: return

        assignmentScheduler.stop(schedulingInfo.taskId)
        schedulingInfo.status = SchedulingStatus.COMPLETED

        ctx.setMutated()
    }
}
