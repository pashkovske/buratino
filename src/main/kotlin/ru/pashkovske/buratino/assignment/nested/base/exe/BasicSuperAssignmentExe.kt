package ru.pashkovske.buratino.assignment.nested.base.exe

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.exe.AssignmentExe
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.base.exe.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment

abstract class BasicSuperAssignmentExe<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentRepo: AssignmentRepo<SuperA>,
    assignmentScheduler: AssignmentTaskScheduler,
    protected val nestedAssignmentExe: AssignmentExe<Nested>
): BasicAssignmentExe<SuperA>(
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    protected fun isNestedCompleted(ctx: ExeCtx<SuperA>): Boolean {
        return ctx.assignment.nested.status == AssignmentStatus.COMPLETED
    }

    protected fun checkAndStartNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.status != AssignmentStatus.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is in ${assignment.nested.status} status, skipping start")
            return
        }
        log.info("Starting nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.start(assignment.nested)
        ctx.setMutated()
    }

    protected fun refreshNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        assignment.nested = nestedAssignmentExe.refresh(assignment.nested.id)
        ctx.setMutated()
    }

    protected fun cancelNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.status == AssignmentStatus.COMPLETED) {
            log.warn("Nested assignment ${assignment.nested.id} is already completed, skipping cancel nested")
            return
        }
        if (assignment.nested.status == AssignmentStatus.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is not started, skipping cancel nested")
            return
        }
        log.info("Canceling nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
        ctx.setMutated()
    }
}
