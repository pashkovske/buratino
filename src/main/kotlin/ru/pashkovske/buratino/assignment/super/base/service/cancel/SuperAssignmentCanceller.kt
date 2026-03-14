package ru.pashkovske.buratino.assignment.`super`.base.service.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.cancel.BasicAssignmentCanceller
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

abstract class SuperAssignmentCanceller<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<SuperA>,
    taskScheduler: TaskScheduler,
    protected val nestedAssignmentExe: AssignmentExe<Nested>
) : BasicAssignmentCanceller<SuperA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doCancel(ctx: ExeCtx<SuperA>) {
        cancelNested(ctx)
    }

    protected fun cancelNested(ctx: ExeCtx<SuperA>) {
        val assignment: SuperA = ctx.assignment
        if (assignment.nested.state == AssignmentState.COMPLETED) {
            log.warn("Nested assignment ${assignment.nested.id} is already completed, skipping cancel nested")
            return
        }
        if (assignment.nested.state == AssignmentState.QUEUED) {
            log.warn("Nested assignment ${assignment.nested.id} is not started, skipping cancel nested")
            return
        }
        log.info("Canceling nested assignment: ${assignment.nested.id}")
        assignment.nested = nestedAssignmentExe.cancel(assignment.nested.id)
        ctx.setMutated()
    }
}
